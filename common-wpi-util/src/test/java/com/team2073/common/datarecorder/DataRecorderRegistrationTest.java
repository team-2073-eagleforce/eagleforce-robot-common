package com.team2073.common.datarecorder;

import com.team2073.common.datarecorder.DataRecorderTestFixtures.BasicRecordable;
import com.team2073.common.datarecorder.DataRecorderTestFixtures.ComplexObjectFixture;
import com.team2073.common.datarecorder.model.DataRecordTable;
import com.team2073.common.datarecorder.model.FieldMapping;
import com.team2073.common.datarecorder.model.LifecycleAwareRecordable;
import com.team2073.common.test.annon.TestFeatureNotImplementedYet;
import com.team2073.common.test.annon.TestNotWrittenYet;
import com.team2073.common.util.StreamUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @author Preston Briggs
 */
public class DataRecorderRegistrationTest extends BaseDataRecorderTest  {

    @Test
    void registerRecordableInstance() {
        cleanRecorder.registerRecordable(cleanBasicRecordable);
        List<Object> recordableList = cleanRecorder.getRegisteredRecordables();

        assertThat(recordableList).isNotEmpty();

        Object registeredRecordable = recordableList.iterator().next();

        assertThat(registeredRecordable).isEqualTo(cleanBasicRecordable);
    }

    @Test
    void registerObjectInstance() {
        cleanRecorder.registerRecordable(cleanComplexObject);
        List<Object> recordableList = cleanRecorder.getRegisteredRecordables();

        assertThat(recordableList).isNotEmpty();

        Object registeredRecordable = recordableList.iterator().next();

        assertThat(registeredRecordable).isEqualTo(cleanComplexObject);
    }

    @Test
    void registerCollection() {
        ArrayList<Object> registerList = new ArrayList<>();
        BasicRecordable basicRecordable1 = new BasicRecordable();
        BasicRecordable basicRecordable2 = new BasicRecordable();
        BasicRecordable basicRecordable3 = new BasicRecordable();
        registerList.add(basicRecordable1);
        registerList.add(basicRecordable2);
        registerList.add(basicRecordable3);
        cleanRecorder.registerRecordable(registerList);

        List<Object> recordableList = cleanRecorder.getRegisteredRecordables();

        assertThat(recordableList).size().isEqualTo(3);
        assertThat(recordableList).containsExactlyInAnyOrder(basicRecordable1, basicRecordable2, basicRecordable3);
    }

    @Test
    void registerArray() {
        Object[] registerList = new Object[3];
        BasicRecordable basicRecordable1 = new BasicRecordable();
        BasicRecordable basicRecordable2 = new BasicRecordable();
        BasicRecordable basicRecordable3 = new BasicRecordable();
        registerList[0] = basicRecordable1;
        registerList[1] = basicRecordable2;
        registerList[2] = basicRecordable3;
        cleanRecorder.registerRecordable(registerList);

        List<Object> recordableList = cleanRecorder.getRegisteredRecordables();

        assertThat(recordableList).size().isEqualTo(3);
        assertThat(recordableList).containsExactlyInAnyOrder(basicRecordable1, basicRecordable2, basicRecordable3);
    }

    @Test
    void registerPrimitive() {
        assertThatIllegalArgumentException().isThrownBy(() -> cleanRecorder.registerRecordable(1));
        assertThatIllegalArgumentException().isThrownBy(() -> cleanRecorder.registerRecordable(1L));
        assertThatIllegalArgumentException().isThrownBy(() -> cleanRecorder.registerRecordable(1.2));
    }

    @Test
    void registerPrimitiveWrapper() {
        assertThatIllegalArgumentException().isThrownBy(() -> cleanRecorder.registerRecordable(new Integer(1)));
        assertThatIllegalArgumentException().isThrownBy(() -> cleanRecorder.registerRecordable(new Long(1)));
        assertThatIllegalArgumentException().isThrownBy(() -> cleanRecorder.registerRecordable(new Double(1.2)));
        assertThatIllegalArgumentException().isThrownBy(() -> cleanRecorder.registerRecordable("foo"));
    }

    @Test
    @TestNotWrittenYet
    void registerRecordableInstanceWithDefaultInterval() {

        // Not important, we can add later

    }

    static class FieldMappingRecordableFixture implements LifecycleAwareRecordable {
        public static final String[] FIELD_NAMES = {"field1", "field2"};
        private String field1;
        private int field2;
    }

    @Test
    @DisplayName("WHEN: LifecycleAwareRecordable instance registered - THEN: Field mappings created")
    void WHEN_RegisterRecordableInstance_THEN_FieldMappingsCreated() {
        LifecycleAwareRecordable recordable = new FieldMappingRecordableFixture();
        cleanRecorder.registerRecordable(recordable);
        List<DataRecordTable> tableList = cleanRecorder.getDataTableList();

        assertThat(tableList).size().isEqualTo(1);

        DataRecordTable table = tableList.iterator().next();
        Object registeredRecordable = table.getRecordable();
        List<FieldMapping> fieldMappingList = table.getFieldMappingList();

        assertThat(fieldMappingList).size().isEqualTo(2);
        assertThat(recordable).isEqualTo(registeredRecordable);
        assertThat(registeredRecordable).isEqualTo(recordable);

        List<String> fieldNames = fieldMappingList.stream().map(it -> it.getField().getName()).collect(Collectors.toList());
        assertThat(fieldNames).containsExactlyInAnyOrder(FieldMappingRecordableFixture.FIELD_NAMES);
    }

    static class FieldMappingObjectFixture implements LifecycleAwareRecordable {
        public static final String[] FIELD_NAMES = {"objField1", "objField2"};
        private String objField1;
        private int objField2;
    }

    @Test
    @DisplayName("WHEN: Object instance registered - THEN: Field mappings created")
    void WHEN_RegisterObjectInstance_THEN_FieldMappingsCreated() {
        Object recordable = new FieldMappingObjectFixture();
        cleanRecorder.registerRecordable(recordable);
        List<DataRecordTable> tableList = cleanRecorder.getDataTableList();

        assertThat(tableList).size().isEqualTo(1);

        DataRecordTable table = tableList.iterator().next();
        Object registeredRecordable = table.getRecordable();
        List<FieldMapping> fieldMappingList = table.getFieldMappingList();

        assertThat(fieldMappingList).size().isEqualTo(2);
        assertThat(recordable).isEqualTo(registeredRecordable);
        assertThat(registeredRecordable).isEqualTo(recordable);

        List<String> fieldNames = fieldMappingList.stream().map(it -> it.getField().getName()).collect(Collectors.toList());
        assertThat(fieldNames).containsExactlyInAnyOrder(FieldMappingObjectFixture.FIELD_NAMES);
    }

    @Test
    @TestFeatureNotImplementedYet
    @TestNotWrittenYet
    @DisplayName("WHEN: LifecycleAwareRecordable registered with custom interval - THEN: Instance only called once per interval duration")
    void registerRecordableInstanceWithCustomInterval() {

    }

    @Test
    @DisplayName("WHEN: Attempt to register same LifecycleAwareRecordable instance multiple times - THEN: Instance actually only registered once and message logged")
    void registerSameRecordableInstanceMultipleTimes() {
        cleanRecorder.registerRecordable(cleanBasicRecordable);
        boolean registered = cleanRecorder.registerRecordable(cleanBasicRecordable);
        assertFalse(registered);
        assertThat(cleanRecorder.getDataTableList()).size().isEqualTo(1);
    }

    @Test
    @DisplayName("WHEN: Attempt to register different LifecycleAwareRecordable instances of same class - THEN: Each instance actually registered individually")
    void registerSameRecordableClassMultipleTimes() {
        cleanRecorder.registerRecordable(new BasicRecordable());
        boolean registered = cleanRecorder.registerRecordable(new BasicRecordable());
        assertTrue(registered);
        assertThat(cleanRecorder.getDataTableList()).size().isEqualTo(2);
    }

    @Test
    @DisplayName("WHEN: Same Object instance registered multiple times - THEN: Instance actually only registered once and message logged")
    void registerSameObjectInstanceMultipleTimes() {
        ComplexObjectFixture nonRecordable = new ComplexObjectFixture();
        cleanRecorder.registerRecordable(nonRecordable);
        boolean registered = cleanRecorder.registerRecordable(nonRecordable);
        assertFalse(registered);
        assertThat(cleanRecorder.getDataTableList()).size().isEqualTo(1);
    }

    @Test
    @DisplayName("WHEN: Same instance registered multiple times, once as a LifecycleAwareRecordable and again as an Object - THEN: Instance actually only registered once and message logged")
    void registerSameObjectInstanceAsRecordableAndAsObject() {
        LifecycleAwareRecordable asRecordable = new BasicRecordable();
        Object asObject = asRecordable;
        cleanRecorder.registerRecordable(asObject);
        boolean registered = cleanRecorder.registerRecordable(asRecordable);
        assertFalse(registered);
        assertThat(cleanRecorder.getDataTableList()).size().isEqualTo(1);
    }

    @Test
    @DisplayName("WHEN: Attempt to register different Object instances of same class - THEN: Each instance actually registered individually")
    void registerSameObjectClassMultipleTimes() {
        cleanRecorder.registerRecordable(new ComplexObjectFixture());
        cleanRecorder.registerRecordable(new ComplexObjectFixture());
        assertThat(cleanRecorder.getDataTableList()).size().isEqualTo(2);
    }

    @Test
    @DisplayName("WHEN: Attempt to register output handler as recorder - THEN: Exception thrown")
    void registerOutputHandlerAsRecordable() {
        String expected = "Attempted to register [InMemoryDataRecordOutputHandler] (a [DataRecordOutputHandler]) for recording. You most likely meant to call [registerOutputHandler(...)]. If you truly wanted to register a [DataRecordOutputHandler] for recording, use the method [registerOutputHandlerAsRecordable(...)].";
        assertThatIllegalArgumentException().isThrownBy(() -> cleanRecorder.registerRecordable(cleanInMemOutputHandler))
                .withMessage(expected);
    }

    @Test
    @DisplayName("WHEN: Attempt to register instance after DataRecorder already started - THEN: Exception is thrown")
    void registerRecordableInstanceAfterAlreadyStarted() {
        DataRecorder basicRecorder = getAndInitBasicRecorder();
        basicRecorder.manualRecord().block();
        assertThatIllegalStateException().isThrownBy(() -> basicRecorder.registerRecordable(cleanBasicRecordable));
    }

    @Test
    @DisplayName("WHEN: Register multiple instances of same class - THEN: Unique names are generated")
    void WHEN_MultipleOfSameClassRegistered_THEN_UniqueNamesAreGenerated() {

        DataRecorder recorder = new DataRecorder();
        recorder.registerRecordable(new BasicRecordable());
        recorder.registerRecordable(new BasicRecordable());
        Collection<DataRecordTable> dataTableMap = recorder.getDataTableList();

        assertThat(dataTableMap).size().isGreaterThanOrEqualTo(2);

        List<? extends String> strings = StreamUtil.mapToList(dataTableMap, it -> it.getName());
        String name1 = strings.get(0);
        String name2 = strings.get(1);

        assertThat(name1).isNotEqualToIgnoringCase(name2);
    }

    @Test
    void registerCustomOutputHandler() {
        DataRecorder basicRecorder = getAndInitBasicRecorder();
        basicRecorder.registerOutputHandler(mockedOutputHandler);

        basicRecorder.manualRecord().block();
        basicRecorder.manualRecord().block();
        basicRecorder.manualFlush().block();

        basicRecorder.manualRecord().block();
        basicRecorder.manualRecord().block();
        basicRecorder.manualFlush().block();

        verify(mockedOutputHandler, times(2)).flushTable(any(), any());
    }

    @Test
    @DisplayName("WHEN: Same DataRecordOutputHandler instance registered multiple times - THEN: Instance actually only registered once and message logged")
    void registerSameOutputHandlerMultipleTimes() {
        cleanRecorder.registerOutputHandler(cleanInMemOutputHandler);
        cleanRecorder.registerOutputHandler(cleanInMemOutputHandler);
        assertThat(cleanRecorder.getOutputHandlerList()).size().isEqualTo(1);
    }
}
