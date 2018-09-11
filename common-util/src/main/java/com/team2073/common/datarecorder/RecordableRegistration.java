package com.team2073.common.datarecorder;

import com.team2073.common.util.EnumUtil;
import com.team2073.common.util.ExceptionUtil;
import com.team2073.common.util.ReflectionUtil;
import com.team2073.common.util.ReflectionUtil.PrimitiveType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.Transient;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author pbriggs
 */
class RecordableRegistration {
    private Logger log = LoggerFactory.getLogger(getClass());

    private final Recordable instance;
    private final long period;
    private final List<FieldMapping> fieldMappingList = new ArrayList<>();

    public RecordableRegistration(Recordable instance, long period) {
        this.instance = instance;
        this.period = period;
        createFieldMapping();
    }

    private void createFieldMapping() {
        List<Field> fields = ReflectionUtil.getInheritedPrivateFields(instance.getClass());
        String transientAnnotationName = "@" + Transient.class.getSimpleName();
        for (Field field : fields) {
            if (field.getAnnotation(Transient.class) != null) {
                log.debug("Skipping field marked with {}: [{}]", transientAnnotationName, field.getName());
                continue;
            }

            String dataPointName = field.getName();
            DataPoint dataPointAnnon = field.getAnnotation(DataPoint.class);
            if (dataPointAnnon != null && !dataPointAnnon.name().equals(DataPoint.NULL)) {
                dataPointName = dataPointAnnon.name();
            }

            // TODO try to get the getter instead

            Class<?> type = field.getType();
            if (!ReflectionUtil.isPrimitiveOrWrapper(type)) {
                ExceptionUtil.illegalState("%s fields must be either primitive or primitive wrapper types. " +
                        "Found field of type [%s] in class [%s]. To ignore a field annotate it with %s.",
                        Recordable.class.getSimpleName(), type.getSimpleName(), instance.getClass().getSimpleName(),
                        transientAnnotationName);
            }

            field.setAccessible(true);

//            ClassUtils.isPrimitiveWrapper()
            PrimitiveType primitiveType = ReflectionUtil.getPrimitiveType(field);

            if (primitiveType == null) {
                ExceptionUtil.illegalState("Could not determine primitive type from field [%s] in class [%s].", field.getName(),
                        instance.getClass().getSimpleName());
            }

            switch (primitiveType.group) {
                case TEXT:
                    fieldMappingList.add(new StringFieldMapping(field, dataPointName, instance));
                    break;

                case DIGIT:
                    fieldMappingList.add(new LongFieldMapping(field, dataPointName, instance));
                    break;

                case DECIMAL:
                    fieldMappingList.add(new DoubleFieldMapping(field, dataPointName, instance));
                    break;

                case BOOLEAN:
                    fieldMappingList.add(new BooleanFieldMapping(field, dataPointName, instance));
                    break;

                default:
                    EnumUtil.throwUnknownValueException(primitiveType.group);
            }

        }
    }

    public Recordable getInstance() {
        return instance;
    }

    public long getPeriod() {
        return period;
    }

    public List<FieldMapping> getFieldMappingList() {
        return fieldMappingList;
    }
}
