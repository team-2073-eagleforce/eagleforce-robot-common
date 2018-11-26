package com.team2073.common.datarecorder;

import com.google.common.base.Strings;
import com.team2073.common.datarecorder.model.DataPoint;
import com.team2073.common.datarecorder.model.DataPointIgnore;
import com.team2073.common.datarecorder.model.EnumDataPoint;
import com.team2073.common.datarecorder.model.FieldMapping;
import com.team2073.common.datarecorder.model.FieldMapping.BooleanFieldMapping;
import com.team2073.common.datarecorder.model.FieldMapping.DoubleFieldMapping;
import com.team2073.common.datarecorder.model.FieldMapping.EnumDataPointFieldMapping;
import com.team2073.common.datarecorder.model.FieldMapping.EnumFieldMapping;
import com.team2073.common.datarecorder.model.FieldMapping.LongFieldMapping;
import com.team2073.common.datarecorder.model.FieldMapping.StringFieldMapping;
import com.team2073.common.datarecorder.model.Recordable;
import com.team2073.common.util.EnumUtil;
import com.team2073.common.util.NameAware;
import com.team2073.common.util.ReflectionUtil;
import com.team2073.common.util.ReflectionUtil.PrimitiveType;
import com.team2073.common.util.Throw;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import static com.team2073.common.util.ClassUtil.*;

/**
 * @author Preston Briggs
 */
public class DataRecorderHelper {

    private static Logger log = LoggerFactory.getLogger(DataRecorderHelper.class);

    private final Map<String, SortedSet<RecordableName>> nameMap = new HashMap<>();
    private final Comparator<RecordableName> nameComparator = Comparator.comparingInt(RecordableName::getIndex);

    public static class RecordableName {
        private final String baseName;
        private final int index;

        public RecordableName(String baseName, int index) {
            this.baseName = baseName;
            this.index = index;
        }

        public String getBaseName() {
            return baseName;
        }

        public int getIndex() {
            return index;
        }

        public String buildName() {
            return index == 1 ? baseName : baseName + index;
        }
    }

    public RecordableName generateName(Object recordable) {
        String name = determineBaseName(recordable);
        SortedSet<RecordableName> resolvedNameSet = nameMap.computeIfAbsent(name, key -> new TreeSet<>(nameComparator));

        RecordableName recordableName;

        if (resolvedNameSet.isEmpty())
            recordableName = new RecordableName(name, 1);
        else
            recordableName = new RecordableName(name, resolvedNameSet.last().index + 1);

        resolvedNameSet.add(recordableName);

        return recordableName;
    }

    private String determineBaseName(Object recordable) {
        Recordable annotation = recordable.getClass().getAnnotation(Recordable.class);

        // 1) Try to get name from dynamic getName() method
        if (recordable instanceof NameAware) {
            NameAware recordable1 = (NameAware) recordable;

            if (!Strings.isNullOrEmpty(recordable1.getName()))
                return recordable1.getName();
        }

        // 2) Try to get name from @Recordable
        if (annotation != null && !Strings.isNullOrEmpty(annotation.name()) && !annotation.name().equals(Recordable.NULL)) {
            return annotation.name();
        }

        // 3) Fallback to classname
        return simpleName(recordable);
    }

    private static class FieldMappingDto {
        final String fieldName;
        final String logPrefix;
        String dataPointName;

        public FieldMappingDto(Field field, String recordableName) {
            fieldName = field.getName();
            logPrefix = "\t\t" + recordableName + "." + fieldName + ":";
            dataPointName = fieldName;
        }
    }

    public List<FieldMapping> createFieldMappings(Object instance) {
        String recordableName = simpleName(instance);
        List<FieldMapping> fieldMappingList = new ArrayList<>();
        Class<? extends Annotation> ignoreFieldAnnotation = DataPointIgnore.class;
        String ignoreDataPointAnnotationName = "@" + ignoreFieldAnnotation.getSimpleName();

        log.info("\tCreating field mapping for Recordable [{}]...", recordableName);

        List<Field> fields = ReflectionUtil.getInheritedPrivateFields(instance.getClass());

        for (Field field : fields) {

            FieldMappingDto mappingInfo = new FieldMappingDto(field, recordableName);

            if (field.getAnnotation(ignoreFieldAnnotation) != null) {
                log.debug("{} Skipping field marked with [{}].", mappingInfo.logPrefix, ignoreDataPointAnnotationName);
                continue;
            }

            DataPoint dataPointAnnon = field.getAnnotation(DataPoint.class);

            if (dataPointAnnon == null) {
                if (Modifier.isStatic(field.getModifiers())) {
                    log.debug("{} Skipping static field.", mappingInfo.logPrefix);
                    continue;
                } else if (Modifier.isFinal(field.getModifiers())) {
                    log.debug("{} Skipping final field.", mappingInfo.logPrefix);
                    continue;
                }
            }

            if (dataPointAnnon != null && !dataPointAnnon.name().equals(DataPoint.NULL)) {
                mappingInfo.dataPointName = dataPointAnnon.name();
                log.debug("{} Registering field with custom field name [{}].", mappingInfo.logPrefix, mappingInfo.dataPointName);
            } else {
                log.trace("{} Registering field.", mappingInfo.logPrefix);
            }

            // TODO try to get the getter instead

            field.setAccessible(true);

            Class<?> type = field.getType();
            FieldMapping mapping = null;

            // TODO: If we wanted to allow adding custom mappers we would do it here, turning this into a list of mappers

            if (ReflectionUtil.isPrimitiveOrWrapper(type)) {
                mapping = createPrimitiveMapping(instance, recordableName, field, mappingInfo);
                validateMapping(mapping, instance, field, "Primitive Mapper");

            } else if(type.isEnum()) {
                mapping = createEnumMapping(instance, recordableName, field, mappingInfo);
                validateMapping(mapping, instance, field, "Enum Mapper");

                // TODO: add additional mappers here

            } else {
                log.debug("{} Could not create field mapping from type [{}]. Skipping...",
                        mappingInfo.logPrefix, type.getSimpleName());
                continue;
            }

            fieldMappingList.add(mapping);
        }

        log.debug("\tCreating field mapping for Recordable [{}] complete.", recordableName);

        return fieldMappingList;
    }

    private void validateMapping(FieldMapping mapping, Object instance, Field field, String mapper) {
        if (mapping == null)
            Throw.illegalState("Field mapper must not return null. Mapper [{}], Recordable: [{}], Field: [{}].",
                    mapper, simpleName(instance), field.getName());
    }

    private FieldMapping createPrimitiveMapping(Object instance, String recordableName, Field field, FieldMappingDto mappingInfo) {
        PrimitiveType primitiveType = ReflectionUtil.getPrimitiveType(field);

        if (primitiveType == null) {
            Throw.illegalState("Could not determine primitive type from field [%s] in class [%s].", mappingInfo.fieldName,
                    recordableName);
        }

        switch (primitiveType.getGroup()) {
            case TEXT:
                log.debug("{} Created String field mapping.", mappingInfo.logPrefix);
                return new StringFieldMapping(field, mappingInfo.dataPointName, instance);

            case DIGIT:
                log.debug("{} Created Long field mapping.", mappingInfo.logPrefix);
                return new LongFieldMapping(field, mappingInfo.dataPointName, instance);

            case DECIMAL:
                log.debug("{} Created Double field mapping.", mappingInfo.logPrefix);
                return new DoubleFieldMapping(field, mappingInfo.dataPointName, instance);

            case BOOLEAN:
                log.debug("{} Created Boolean field mapping.", mappingInfo.logPrefix);
                return new BooleanFieldMapping(field, mappingInfo.dataPointName, instance);

            default:
                EnumUtil.throwUnknownValueException(primitiveType.getGroup());
                return null;
        }
    }

    private FieldMapping createEnumMapping(Object instance, String recordableName, Field field, FieldMappingDto mappingInfo) {
        if (EnumDataPoint.class.isAssignableFrom(field.getType())) {
            log.debug("{} Created EnumDataPointFieldMapping field mapping.", mappingInfo.logPrefix);
            return new EnumDataPointFieldMapping(field, mappingInfo.dataPointName, instance);
        } else {
            log.debug("{} Created EnumFieldMapping field mapping.", mappingInfo.logPrefix);
            return new EnumFieldMapping(field, mappingInfo.dataPointName, instance);
        }
    }

}
