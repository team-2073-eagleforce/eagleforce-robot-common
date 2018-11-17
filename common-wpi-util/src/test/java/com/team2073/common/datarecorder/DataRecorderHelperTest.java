package com.team2073.common.datarecorder;

import com.team2073.common.datarecorder.DataRecorderTestFixtures.*;
import com.team2073.common.datarecorder.model.DataPoint;
import com.team2073.common.datarecorder.model.FieldMapping;
import com.team2073.common.datarecorder.model.LifecycleAwareRecordable;
import com.team2073.common.datarecorder.model.Recordable;
import com.team2073.common.test.annon.TestFeatureNotImplementedYet;
import com.team2073.common.util.NameAware;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.team2073.common.datarecorder.DataRecorderTestFixtures.*;
import static org.assertj.core.api.Assertions.*;

/**
 * @author Preston Briggs
 */
class DataRecorderHelperTest extends BaseDataRecorderTest {

    @Recordable(name = NamedRecordable.CUSTOM_NAME)
    public static class NamedRecordable {
        private static final String CUSTOM_NAME = "FooBar";
        private int numField = 1;
    }

    @Test
    public void verifyCustomNameUsed() {
        Object recordable = new NamedRecordable();
        String generatedName = cleanMapper.generateName(recordable).getBaseName();
        assertThat(generatedName).isEqualToIgnoringCase(NamedRecordable.CUSTOM_NAME);
    }

    public static class NamedSubclassRecordable extends NamedRecordable {
    }

    @Test
    public void verifyCustomNameUsedFromSubclass() {
        Object recordable = new NamedSubclassRecordable();
        String generatedName = cleanMapper.generateName(recordable).getBaseName();
        assertThat(generatedName).isEqualToIgnoringCase(NamedRecordable.CUSTOM_NAME);
    }

    public static class DynamicallyNamedRecordableFixture implements NameAware {
        private static final String CUSTOM_NAME = "Baz";
        @Override
        public String getName() {
            return CUSTOM_NAME;
        }
    }

    @Test
    public void verifyDynamicNameUsed() {
        Object recordable = new DynamicallyNamedRecordableFixture();
        String generatedName = cleanMapper.generateName(recordable).getBaseName();
        assertThat(generatedName).isEqualToIgnoringCase(DynamicallyNamedRecordableFixture.CUSTOM_NAME);
    }

    @Recordable
    public static class AnnotatedRecordableFixture { }

    @Test
    public void verifyNullNameNeverUsed() {
        Object recordable = new AnnotatedRecordableFixture();
        String generatedName = cleanMapper.generateName(recordable).getBaseName();
        assertThat(generatedName).isNotEqualTo(Recordable.NULL);
    }

    public static class PrimitiveOnly_RecordableFixture implements LifecycleAwareRecordable {
        private boolean booleanField = true;
        private char charField = 'a';
        private byte byteField = 1;
        private short shortField = 2;
        private int intField = 3;
        private long longField = 4;
        private float floatField = 5.1f;
        private double doubleField = 6.1;
    }


    @Test
    public void verifyAllPrimitiveTypes_DoMap() {
        Object recordable = new PrimitiveOnly_RecordableFixture();

        List<FieldMapping> fieldMappings = cleanMapper.createFieldMappings(recordable);

        List<Class<?>> fieldClassList = fieldMappings.stream().map(it -> it.getField().getType()).collect(Collectors.toList());

        assertThat(fieldClassList).contains(boolean.class);
        assertThat(fieldClassList).contains(char.class);
        assertThat(fieldClassList).contains(byte.class);
        assertThat(fieldClassList).contains(short.class);
        assertThat(fieldClassList).contains(int.class);
        assertThat(fieldClassList).contains(long.class);
        assertThat(fieldClassList).contains(float.class);
        assertThat(fieldClassList).contains(double.class);
    }

    public static class PrimitiveWrapperOnly_RecordableFixture implements LifecycleAwareRecordable {
        private Boolean booleanField = true;
        private Character charField = 'a';
        private Byte byteField = 1;
        private Short shortField = 2;
        private Integer intField = 3;
        private Long longField = 4L;
        private Float floatField = 5.1f;
        private Double doubleField = 6.1;
    }

    @Test
    void verifyAllPrimitiveWrapperTypes_DoMap() {
        Object recordable = new PrimitiveWrapperOnly_RecordableFixture();

        List<FieldMapping> fieldMappings = cleanMapper.createFieldMappings(recordable);

        List<Class<?>> fieldClassList = fieldMappings.stream().map(it -> it.getField().getType()).collect(Collectors.toList());

        assertThat(fieldClassList).contains(Boolean.class);
        assertThat(fieldClassList).contains(Character.class);
        assertThat(fieldClassList).contains(Byte.class);
        assertThat(fieldClassList).contains(Short.class);
        assertThat(fieldClassList).contains(Integer.class);
        assertThat(fieldClassList).contains(Long.class);
        assertThat(fieldClassList).contains(Float.class);
        assertThat(fieldClassList).contains(Double.class);
    }

    public static class String_RecordableFixture implements LifecycleAwareRecordable {
        private String stringField = "hello";
    }

    @Test
    void verifyStringTypes_DoMap() {
        Object recordable = new String_RecordableFixture();

        List<FieldMapping> fieldMappings = cleanMapper.createFieldMappings(recordable);

        List<Class<?>> fieldClassList = fieldMappings.stream().map(it -> it.getField().getType()).collect(Collectors.toList());

        assertThat(fieldClassList).contains(String.class);
    }

    public static class Enum_RecordableFixture implements LifecycleAwareRecordable {
        private SimpleEnum enumField = SimpleEnum.FIELD_ONE;
    }

    @Test
    void verifyEnumTypes_DoMap() {
        Object recordable = new Enum_RecordableFixture();

        List<FieldMapping> fieldMappings = cleanMapper.createFieldMappings(recordable);

        List<Class<?>> fieldClassList = fieldMappings.stream().map(it -> it.getField().getType()).collect(Collectors.toList());

        assertThat(fieldClassList).contains(SimpleEnum.class);
    }

    public static class CustomDataPointEnum_RecordableFixture implements LifecycleAwareRecordable {
        private CustomDataPointEnum customDataPointEnumField = CustomDataPointEnum.TWO;
    }

    @Test
    void verifyEnumDataPointTypes_DoMap() {
        Object recordable = new CustomDataPointEnum_RecordableFixture();

        List<FieldMapping> fieldMappings = cleanMapper.createFieldMappings(recordable);

        List<Class<?>> fieldClassList = fieldMappings.stream().map(it -> it.getField().getType()).collect(Collectors.toList());

        assertThat(fieldClassList).contains(CustomDataPointEnum.class);

        List<String> fieldValueList = fieldMappings.stream().map(it -> it.fieldToString()).collect(Collectors.toList());

        assertThat(fieldValueList).contains(String.valueOf(CONTROL_DATA));
    }

    public static class OptionalOfNonComplex_RecordableFixture implements LifecycleAwareRecordable {
        private Optional<Long> optionalOfNonComplexField = Optional.of(3L);
    }

    @Test
    @TestFeatureNotImplementedYet
    void verifyOptionalOfNonComplexObjectTypes_DoMap() {
        Object recordable = new OptionalOfNonComplex_RecordableFixture();

        List<FieldMapping> fieldMappings = cleanMapper.createFieldMappings(recordable);

        List<Class<?>> fieldClassList = fieldMappings.stream().map(it -> it.getField().getType()).collect(Collectors.toList());

        assertThat(fieldClassList).contains(Long.class);
    }

    public static class OptionalOfComplex_RecordableFixture implements LifecycleAwareRecordable {
        @DataPoint
        private Optional<ComplexObjectFixture> optionalOfNonComplexField = Optional.of(new ComplexObjectFixture());
    }

    @Test
    @TestFeatureNotImplementedYet
    void verifyOptionalOfComplexObjectTypes_DoMap() {
        Object recordable = new OptionalOfComplex_RecordableFixture();

        List<FieldMapping> fieldMappings = cleanMapper.createFieldMappings(recordable);

        List<Class<?>> fieldClassList = fieldMappings.stream().map(it -> it.getField().getType()).collect(Collectors.toList());

        assertThat(fieldClassList).contains(ComplexObjectFixture.class);
    }

    public static class InnerClass_RecordableFixture {
        private boolean booleanField = true;
        @DataPoint
        private InnerClass innerClassField = new InnerClass();

        public class InnerClass {
            private int intField = 3;
            private long longField = 4;
        }
    }

    @Test
    @TestFeatureNotImplementedYet
    void verifyInnerClassTypes_DoMap() {
        Object recordable = new InnerClass_RecordableFixture();

        List<FieldMapping> fieldMappings = cleanMapper.createFieldMappings(recordable);

        List<Class<?>> fieldClassList = fieldMappings.stream().map(it -> it.getField().getType()).collect(Collectors.toList());

        assertThat(fieldClassList).containsExactlyInAnyOrder(boolean.class, int.class, long.class);
    }

    public static class Array_RecordableFixture {
        private int[] primitiveArrayField = new int[]{1, 2, 3};
        private Integer[] primitiveWrapperArrayField = new Integer[]{1, 2, 3};
        private ComplexObjectFixture[] complexObjectArrayField = new ComplexObjectFixture[]{new ComplexObjectFixture(), new ComplexObjectFixture()};
    }

    @Test
    void verifyArrayTypes_DoNotMap() {
        Object recordable = new Array_RecordableFixture();

        List<FieldMapping> fieldMappings = cleanMapper.createFieldMappings(recordable);

        List<Class<?>> fieldClassList = fieldMappings.stream().map(it -> it.getField().getType()).collect(Collectors.toList());

        assertThat(fieldClassList).isEmpty();
    }

    public static class Collection_RecordableFixture {
        private Collection<Integer> collectionField = new ArrayList<>();
    }

    @Test
    void verifyCollectionTypes_DoNotMap() {
        Object recordable = new Collection_RecordableFixture();

        List<FieldMapping> fieldMappings = cleanMapper.createFieldMappings(recordable);

        List<Class<?>> fieldClassList = fieldMappings.stream().map(it -> it.getField().getType()).collect(Collectors.toList());

        assertThat(fieldClassList).isEmpty();
    }

    public static class FinalField_RecordableFixture {
        private final int finalField = 1;
    }

    @Test
    void verifyFinalFields_DoNotMap() {
        Object recordable = new FinalField_RecordableFixture();

        List<FieldMapping> fieldMappings = cleanMapper.createFieldMappings(recordable);

        assertThat(fieldMappings).isEmpty();
    }

    public static class StaticField_RecordableFixture {
        private static int finalField = 1;
    }

    @Test
    void verifyStaticFields_DoNotMap() {
        Object recordable = new StaticField_RecordableFixture();

        List<FieldMapping> fieldMappings = cleanMapper.createFieldMappings(recordable);

        assertThat(fieldMappings).isEmpty();
    }

    public static class BiDirectionalA_RecordableFixture {
        private long longField = 3L;
        private BiDirectionalB_RecordableFixture biDirectionalField = new BiDirectionalB_RecordableFixture(this);
    }

    public static class BiDirectionalB_RecordableFixture {
        private double doubleField = 4.0;
        private final BiDirectionalA_RecordableFixture biDirectionalField;

        public BiDirectionalB_RecordableFixture(BiDirectionalA_RecordableFixture biDirectionalField) {
            this.biDirectionalField = biDirectionalField;
        }
    }

    @Test
    @TestFeatureNotImplementedYet
    void verifyBiDirectionalRelationships_DontInfiniteLoop() {
        try {
            Object recordable = new OptionalOfComplex_RecordableFixture();

            List<FieldMapping> fieldMappings = cleanMapper.createFieldMappings(recordable);

            List<Class<?>> fieldClassList = fieldMappings.stream().map(it -> it.getField().getType()).collect(Collectors.toList());

            assertThat(fieldClassList).containsExactlyInAnyOrder(long.class, double.class);

        } catch (StackOverflowError error) {
            fail("Recursive bi-directional relationship not handled properly.", error);
        }
    }

}