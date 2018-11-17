package com.team2073.common.datarecorder;

/**
 * @author Preston Briggs
 */
public class DataRecorderCreateFieldMappingUnitTest {
//
//    public static class Basic_RecordableFixture {
//        private boolean booleanField = true;
//        private char charField = 'a';
//        private byte byteField = 1;
//        private short shortField = 2;
//        private int intField = 3;
//        private long longField = 4;
//        private float floatField = 5.1f;
//        private double doubleField = 6.1;
//
//        private Boolean booleanWrapperField = false;
//        private Character charWrapperField = 'b';
//        private Byte byteWrapperField = 11;
//        private Short shortWrapperField = 22;
//        private Integer intWrapperField = 33;
//        private Long longWrapperField = 44L;
//        private Float floatWrapperField = 55.1f;
//        private Double doubleWrapperField = 66.1;
//
//        private String stringField = "hello";
//        private State enumField = State.NEW;
//        private ComplexObjectFixture complexField = new ComplexObjectFixture();
//        private InnerClass innerClassField = new InnerClass();
//        private Optional<Long> optionalOfRecordableField = Optional.of(123L);
//        private Optional<ComplexObjectFixture> optionalOfNonRecordableField = Optional.of(new ComplexObjectFixture());
//
//        private int[] arrayField = new int[]{1, 2, 3};
//        private Collection<Integer> collectionField = new ArrayList<>();
//
//        public class InnerClass {
//            private boolean booleanInnerField = true;
//            private char charInnerField = 'a';
//            private byte byteInnerField = 1;
//            private File complexInnerField = new File("");
//        }
//    }
//
//    @Recordable
//    public static class Annotated_RecordableFixture {
//        private boolean booleanField = true;
//        private char charField = 'a';
//        private byte byteField = 1;
//        private short shortField = 2;
//        private int intField = 3;
//        private long longField = 4;
//        private float floatField = 5.1f;
//        private double doubleField = 6.1;
//
//        private Boolean booleanWrapperField = false;
//        private Character charWrapperField = 'b';
//        private Byte byteWrapperField = 11;
//        private Short shortWrapperField = 22;
//        private Integer intWrapperField = 33;
//        private Long longWrapperField = 44L;
//        private Float floatWrapperField = 55.1f;
//        private Double doubleWrapperField = 66.1;
//
//        private String stringField = "hello";
//        private State enumField = State.NEW;
//        private ComplexObjectFixture complexField = new ComplexObjectFixture();
//        private InnerClass innerClassField = new InnerClass();
//        private Optional<Long> optionalOfRecordableField = Optional.of(123L);
//        private Optional<ComplexObjectFixture> optionalOfNonRecordableField = Optional.of(new ComplexObjectFixture());
//
//        private int[] arrayField = new int[]{1, 2, 3};
//        private Collection<Integer> collectionField = new ArrayList<>();
//
//        public class InnerClass {
//            private boolean booleanInnerField = true;
//            private char charInnerField = 'a';
//            private byte byteInnerField = 1;
//            private File complexInnerField = new File("");
//        }
//    }
//
//    @Recordable(inclusionMode = InclusionMode.INCLUDE_ALL)
//    public static class AnnotatedWithIncludeAll_RecordableFixture {
//        private boolean booleanField = true;
//        private char charField = 'a';
//        private byte byteField = 1;
//        private short shortField = 2;
//        private int intField = 3;
//        private long longField = 4;
//        private float floatField = 5.1f;
//        private double doubleField = 6.1;
//
//        private Boolean booleanWrapperField = false;
//        private Character charWrapperField = 'b';
//        private Byte byteWrapperField = 11;
//        private Short shortWrapperField = 22;
//        private Integer intWrapperField = 33;
//        private Long longWrapperField = 44L;
//        private Float floatWrapperField = 55.1f;
//        private Double doubleWrapperField = 66.1;
//
//        private String stringField = "hello";
//        private State enumField = State.NEW;
//        private ComplexObjectFixture complexField = new ComplexObjectFixture();
//        private InnerClass innerClassField = new InnerClass();
//        private Optional<Long> optionalOfRecordableField = Optional.of(123L);
//        private Optional<ComplexObjectFixture> optionalOfNonRecordableField = Optional.of(new ComplexObjectFixture());
//
//        private int[] arrayField = new int[]{1, 2, 3};
//        private Collection<Integer> collectionField = new ArrayList<>();
//
//        public class InnerClass {
//            private boolean booleanInnerField = true;
//            private char charInnerField = 'a';
//            private byte byteInnerField = 1;
//            private File complexInnerField = new File("");
//        }
//    }
//
//    @Recordable(inclusionMode = InclusionMode.EXCLUDE_ALL)
//    public static class AnnotatedWithExcludeAll_RecordableFixture {
//        private boolean booleanField = true;
//        private char charField = 'a';
//        private byte byteField = 1;
//        private short shortField = 2;
//        private int intField = 3;
//        private long longField = 4;
//        private float floatField = 5.1f;
//        private double doubleField = 6.1;
//
//        private Boolean booleanWrapperField = false;
//        private Character charWrapperField = 'b';
//        private Byte byteWrapperField = 11;
//        private Short shortWrapperField = 22;
//        private Integer intWrapperField = 33;
//        private Long longWrapperField = 44L;
//        private Float floatWrapperField = 55.1f;
//        private Double doubleWrapperField = 66.1;
//
//        private String stringField = "hello";
//        private State enumField = State.NEW;
//        private ComplexObjectFixture complexField = new ComplexObjectFixture();
//        private InnerClass innerClassField = new InnerClass();
//        private Optional<Long> optionalOfRecordableField = Optional.of(123L);
//        private Optional<ComplexObjectFixture> optionalOfNonRecordableField = Optional.of(new ComplexObjectFixture());
//
//        private int[] arrayField = new int[]{1, 2, 3};
//        private Collection<Integer> collectionField = new ArrayList<>();
//
//        public class InnerClass {
//            private boolean booleanInnerField = true;
//            private char charInnerField = 'a';
//            private byte byteInnerField = 1;
//            private File complexInnerField = new File("");
//        }
//    }
//
//    @Recordable(inclusionMode = InclusionMode.INCLUDE_ALL_FAIL_FAST)
//    public static class AnnotatedWithIncludeAllFailFast_RecordableFixture {
//        private boolean booleanField = true;
//        private char charField = 'a';
//        private byte byteField = 1;
//        private short shortField = 2;
//        private int intField = 3;
//        private long longField = 4;
//        private float floatField = 5.1f;
//        private double doubleField = 6.1;
//
//        private Boolean booleanWrapperField = false;
//        private Character charWrapperField = 'b';
//        private Byte byteWrapperField = 11;
//        private Short shortWrapperField = 22;
//        private Integer intWrapperField = 33;
//        private Long longWrapperField = 44L;
//        private Float floatWrapperField = 55.1f;
//        private Double doubleWrapperField = 66.1;
//
//        private String stringField = "hello";
//        private State enumField = State.NEW;
////        private ComplexObjectFixture complexField = new ComplexObjectFixture();
////        private InnerClass innerClassField = new InnerClass();
//        private Optional<Long> optionalOfRecordableField = Optional.of(123L);
////        private Optional<ComplexObjectFixture> optionalOfNonRecordableField = Optional.of(new ComplexObjectFixture());
//
////        private int[] arrayField = new int[]{1, 2, 3};
////        private Collection<Integer> collectionField = new ArrayList<>();
//
//        public class InnerClass {
//            private boolean booleanInnerField = true;
//            private char charInnerField = 'a';
//            private byte byteInnerField = 1;
//            private File complexInnerField = new File("");
//        }
//    }
//
//    //
//
//    public static class Basic_FieldsAnnotated_RecordableFixture {
//        @DataPoint private boolean booleanField = true;
//        @DataPoint private char charField = 'a';
//        @DataPoint private byte byteField = 1;
//        @DataPoint private short shortField = 2;
//        @DataPoint private int intField = 3;
//        @DataPoint private long longField = 4;
//        @DataPoint private float floatField = 5.1f;
//        @DataPoint private double doubleField = 6.1;
//
//        @DataPoint private Boolean booleanWrapperField = false;
//        @DataPoint private Character charWrapperField = 'b';
//        @DataPoint private Byte byteWrapperField = 11;
//        @DataPoint private Short shortWrapperField = 22;
//        @DataPoint private Integer intWrapperField = 33;
//        @DataPoint private Long longWrapperField = 44L;
//        @DataPoint private Float floatWrapperField = 55.1f;
//        @DataPoint private Double doubleWrapperField = 66.1;
//
//        @DataPoint private String stringField = "hello";
//        @DataPoint private State enumField = State.NEW;
////        @DataPoint private ComplexObjectFixture complexField = new ComplexObjectFixture();
////        @DataPoint private InnerClass innerClassField = new InnerClass();
//        @DataPoint private Optional<Long> optionalOfRecordableField = Optional.of(123L);
////        @DataPoint private Optional<ComplexObjectFixture> optionalOfNonRecordableField = Optional.of(new ComplexObjectFixture());
//
////        @DataPoint private int[] arrayField = new int[]{1, 2, 3};
////        @DataPoint private Collection<Integer> collectionField = new ArrayList<>();
//
//        public class InnerClass {
//            private boolean booleanInnerField = true;
//            private char charInnerField = 'a';
//            private byte byteInnerField = 1;
//            private File complexInnerField = new File("");
//        }
//    }
//
//    @Recordable
//    public static class Annotated_FieldsAnnotated_RecordableFixture {
//        @DataPoint private boolean booleanField = true;
//        @DataPoint private char charField = 'a';
//        @DataPoint private byte byteField = 1;
//        @DataPoint private short shortField = 2;
//        @DataPoint private int intField = 3;
//        @DataPoint private long longField = 4;
//        @DataPoint private float floatField = 5.1f;
//        @DataPoint private double doubleField = 6.1;
//
//        @DataPoint private Boolean booleanWrapperField = false;
//        @DataPoint private Character charWrapperField = 'b';
//        @DataPoint private Byte byteWrapperField = 11;
//        @DataPoint private Short shortWrapperField = 22;
//        @DataPoint private Integer intWrapperField = 33;
//        @DataPoint private Long longWrapperField = 44L;
//        @DataPoint private Float floatWrapperField = 55.1f;
//        @DataPoint private Double doubleWrapperField = 66.1;
//
//        @DataPoint private String stringField = "hello";
//        @DataPoint private State enumField = State.NEW;
////        @DataPoint private ComplexObjectFixture complexField = new ComplexObjectFixture();
////        @DataPoint private InnerClass innerClassField = new InnerClass();
//        @DataPoint private Optional<Long> optionalOfRecordableField = Optional.of(123L);
////        @DataPoint private Optional<ComplexObjectFixture> optionalOfNonRecordableField = Optional.of(new ComplexObjectFixture());
//
////        @DataPoint private int[] arrayField = new int[]{1, 2, 3};
////        @DataPoint private Collection<Integer> collectionField = new ArrayList<>();
//
//        public class InnerClass {
//            private boolean booleanInnerField = true;
//            private char charInnerField = 'a';
//            private byte byteInnerField = 1;
//            private File complexInnerField = new File("");
//        }
//    }
//
//    @Recordable(inclusionMode = InclusionMode.INCLUDE_ALL)
//    public static class AnnotatedWithIncludeAll_FieldsAnnotated_RecordableFixture {
//        @DataPoint private boolean booleanField = true;
//        @DataPoint private char charField = 'a';
//        @DataPoint private byte byteField = 1;
//        @DataPoint private short shortField = 2;
//        @DataPoint private int intField = 3;
//        @DataPoint private long longField = 4;
//        @DataPoint private float floatField = 5.1f;
//        @DataPoint private double doubleField = 6.1;
//
//        @DataPoint private Boolean booleanWrapperField = false;
//        @DataPoint private Character charWrapperField = 'b';
//        @DataPoint private Byte byteWrapperField = 11;
//        @DataPoint private Short shortWrapperField = 22;
//        @DataPoint private Integer intWrapperField = 33;
//        @DataPoint private Long longWrapperField = 44L;
//        @DataPoint private Float floatWrapperField = 55.1f;
//        @DataPoint private Double doubleWrapperField = 66.1;
//
//        @DataPoint private String stringField = "hello";
//        @DataPoint private State enumField = State.NEW;
////        @DataPoint private ComplexObjectFixture complexField = new ComplexObjectFixture();
////        @DataPoint private InnerClass innerClassField = new InnerClass();
//        @DataPoint private Optional<Long> optionalOfRecordableField = Optional.of(123L);
////        @DataPoint private Optional<ComplexObjectFixture> optionalOfNonRecordableField = Optional.of(new ComplexObjectFixture());
//
////        @DataPoint private int[] arrayField = new int[]{1, 2, 3};
////        @DataPoint private Collection<Integer> collectionField = new ArrayList<>();
//
//        public class InnerClass {
//            private boolean booleanInnerField = true;
//            private char charInnerField = 'a';
//            private byte byteInnerField = 1;
//            private File complexInnerField = new File("");
//        }
//    }
//
//    @Recordable(inclusionMode = InclusionMode.EXCLUDE_ALL)
//    public static class AnnotatedWithExcludeAll_FieldsAnnotated_RecordableFixture {
//        @DataPoint private boolean booleanField = true;
//        @DataPoint private char charField = 'a';
//        @DataPoint private byte byteField = 1;
//        @DataPoint private short shortField = 2;
//        @DataPoint private int intField = 3;
//        @DataPoint private long longField = 4;
//        @DataPoint private float floatField = 5.1f;
//        @DataPoint private double doubleField = 6.1;
//
//        @DataPoint private Boolean booleanWrapperField = false;
//        @DataPoint private Character charWrapperField = 'b';
//        @DataPoint private Byte byteWrapperField = 11;
//        @DataPoint private Short shortWrapperField = 22;
//        @DataPoint private Integer intWrapperField = 33;
//        @DataPoint private Long longWrapperField = 44L;
//        @DataPoint private Float floatWrapperField = 55.1f;
//        @DataPoint private Double doubleWrapperField = 66.1;
//
//        @DataPoint private String stringField = "hello";
//        @DataPoint private State enumField = State.NEW;
////        @DataPoint private ComplexObjectFixture complexField = new ComplexObjectFixture();
////        @DataPoint private InnerClass innerClassField = new InnerClass();
//        @DataPoint private Optional<Long> optionalOfRecordableField = Optional.of(123L);
////        @DataPoint private Optional<ComplexObjectFixture> optionalOfNonRecordableField = Optional.of(new ComplexObjectFixture());
//
////        @DataPoint private int[] arrayField = new int[]{1, 2, 3};
////        @DataPoint private Collection<Integer> collectionField = new ArrayList<>();
//
//        public class InnerClass {
//            private boolean booleanInnerField = true;
//            private char charInnerField = 'a';
//            private byte byteInnerField = 1;
//            private File complexInnerField = new File("");
//        }
//    }
//
//    @Recordable(inclusionMode = InclusionMode.INCLUDE_ALL_FAIL_FAST)
//    public static class AnnotatedWithIncludeAllFailFast_FieldsAnnotated_RecordableFixture {
//        @DataPoint private boolean booleanField = true;
//        @DataPoint private char charField = 'a';
//        @DataPoint private byte byteField = 1;
//        @DataPoint private short shortField = 2;
//        @DataPoint private int intField = 3;
//        @DataPoint private long longField = 4;
//        @DataPoint private float floatField = 5.1f;
//        @DataPoint private double doubleField = 6.1;
//
//        @DataPoint private Boolean booleanWrapperField = false;
//        @DataPoint private Character charWrapperField = 'b';
//        @DataPoint private Byte byteWrapperField = 11;
//        @DataPoint private Short shortWrapperField = 22;
//        @DataPoint private Integer intWrapperField = 33;
//        @DataPoint private Long longWrapperField = 44L;
//        @DataPoint private Float floatWrapperField = 55.1f;
//        @DataPoint private Double doubleWrapperField = 66.1;
//
//        @DataPoint private String stringField = "hello";
//        @DataPoint private State enumField = State.NEW;
////        @DataPoint private ComplexObjectFixture complexField = new ComplexObjectFixture();
////        @DataPoint private InnerClass innerClassField = new InnerClass();
//        @DataPoint private Optional<Long> optionalOfRecordableField = Optional.of(123L);
////        @DataPoint private Optional<ComplexObjectFixture> optionalOfNonRecordableField = Optional.of(new ComplexObjectFixture());
//
////        @DataPoint private int[] arrayField = new int[]{1, 2, 3};
////        @DataPoint private Collection<Integer> collectionField = new ArrayList<>();
//
//        public class InnerClass {
//            private boolean booleanInnerField = true;
//            private char charInnerField = 'a';
//            private byte byteInnerField = 1;
//            private File complexInnerField = new File("");
//        }
//    }
//
//    //
//
//    //
//
////    public static class Basic_FieldsAnnotatedWithIncluded_RecordableFixture {
////        @DataPoint(include = true) private boolean booleanField = true;
////        @DataPoint(include = true) private char charField = 'a';
////        @DataPoint(include = true) private byte byteField = 1;
////        @DataPoint(include = true) private short shortField = 2;
////        @DataPoint(include = true) private int intField = 3;
////        @DataPoint(include = true) private long longField = 4;
////        @DataPoint(include = true) private float floatField = 5.1f;
////        @DataPoint(include = true) private double doubleField = 6.1;
////
////        @DataPoint(include = true) private Boolean booleanWrapperField = false;
////        @DataPoint(include = true) private Character charWrapperField = 'b';
////        @DataPoint(include = true) private Byte byteWrapperField = 11;
////        @DataPoint(include = true) private Short shortWrapperField = 22;
////        @DataPoint(include = true) private Integer intWrapperField = 33;
////        @DataPoint(include = true) private Long longWrapperField = 44L;
////        @DataPoint(include = true) private Float floatWrapperField = 55.1f;
////        @DataPoint(include = true) private Double doubleWrapperField = 66.1;
////
////        @DataPoint(include = true) private String stringField = "hello";
////        @DataPoint(include = true) private State enumField = State.NEW;
//////        @DataPoint(include = true) private ComplexObjectFixture complexField = new ComplexObjectFixture();
//////        @DataPoint(include = true) private InnerClass innerClassField = new InnerClass();
////        @DataPoint(include = true) private Optional<Long> optionalOfRecordableField = Optional.of(123L);
//////        @DataPoint(include = true) private Optional<ComplexObjectFixture> optionalOfNonRecordableField = Optional.of(new ComplexObjectFixture());
////
//////        @DataPoint(include = true) private int[] arrayField = new int[]{1, 2, 3};
//////        @DataPoint(include = true) private Collection<Integer> collectionField = new ArrayList<>();
////
////        public class InnerClass {
////            private boolean booleanInnerField = true;
////            private char charInnerField = 'a';
////            private byte byteInnerField = 1;
////            private File complexInnerField = new File("");
////        }
////    }
////
////    @Recordable
////    public static class Annotated_FieldsAnnotatedWithIncluded_RecordableFixture {
////        @DataPoint(include = true) private boolean booleanField = true;
////        @DataPoint(include = true) private char charField = 'a';
////        @DataPoint(include = true) private byte byteField = 1;
////        @DataPoint(include = true) private short shortField = 2;
////        @DataPoint(include = true) private int intField = 3;
////        @DataPoint(include = true) private long longField = 4;
////        @DataPoint(include = true) private float floatField = 5.1f;
////        @DataPoint(include = true) private double doubleField = 6.1;
////
////        @DataPoint(include = true) private Boolean booleanWrapperField = false;
////        @DataPoint(include = true) private Character charWrapperField = 'b';
////        @DataPoint(include = true) private Byte byteWrapperField = 11;
////        @DataPoint(include = true) private Short shortWrapperField = 22;
////        @DataPoint(include = true) private Integer intWrapperField = 33;
////        @DataPoint(include = true) private Long longWrapperField = 44L;
////        @DataPoint(include = true) private Float floatWrapperField = 55.1f;
////        @DataPoint(include = true) private Double doubleWrapperField = 66.1;
////
////        @DataPoint(include = true) private String stringField = "hello";
////        @DataPoint(include = true) private State enumField = State.NEW;
//////        @DataPoint(include = true) private ComplexObjectFixture complexField = new ComplexObjectFixture();
//////        @DataPoint(include = true) private InnerClass innerClassField = new InnerClass();
////        @DataPoint(include = true) private Optional<Long> optionalOfRecordableField = Optional.of(123L);
//////        @DataPoint(include = true) private Optional<ComplexObjectFixture> optionalOfNonRecordableField = Optional.of(new ComplexObjectFixture());
////
//////        @DataPoint(include = true) private int[] arrayField = new int[]{1, 2, 3};
//////        @DataPoint(include = true) private Collection<Integer> collectionField = new ArrayList<>();
////
////        public class InnerClass {
////            private boolean booleanInnerField = true;
////            private char charInnerField = 'a';
////            private byte byteInnerField = 1;
////            private File complexInnerField = new File("");
////        }
////    }
////
////    @Recordable(inclusionMode = InclusionMode.INCLUDE_ALL)
////    public static class AnnotatedWithIncludeAll_FieldsAnnotatedWithIncluded_RecordableFixture {
////        @DataPoint(include = true) private boolean booleanField = true;
////        @DataPoint(include = true) private char charField = 'a';
////        @DataPoint(include = true) private byte byteField = 1;
////        @DataPoint(include = true) private short shortField = 2;
////        @DataPoint(include = true) private int intField = 3;
////        @DataPoint(include = true) private long longField = 4;
////        @DataPoint(include = true) private float floatField = 5.1f;
////        @DataPoint(include = true) private double doubleField = 6.1;
////
////        @DataPoint(include = true) private Boolean booleanWrapperField = false;
////        @DataPoint(include = true) private Character charWrapperField = 'b';
////        @DataPoint(include = true) private Byte byteWrapperField = 11;
////        @DataPoint(include = true) private Short shortWrapperField = 22;
////        @DataPoint(include = true) private Integer intWrapperField = 33;
////        @DataPoint(include = true) private Long longWrapperField = 44L;
////        @DataPoint(include = true) private Float floatWrapperField = 55.1f;
////        @DataPoint(include = true) private Double doubleWrapperField = 66.1;
////
////        @DataPoint(include = true) private String stringField = "hello";
////        @DataPoint(include = true) private State enumField = State.NEW;
//////        @DataPoint(include = true) private ComplexObjectFixture complexField = new ComplexObjectFixture();
//////        @DataPoint(include = true) private InnerClass innerClassField = new InnerClass();
////        @DataPoint(include = true) private Optional<Long> optionalOfRecordableField = Optional.of(123L);
//////        @DataPoint(include = true) private Optional<ComplexObjectFixture> optionalOfNonRecordableField = Optional.of(new ComplexObjectFixture());
////
//////        @DataPoint(include = true) private int[] arrayField = new int[]{1, 2, 3};
//////        @DataPoint(include = true) private Collection<Integer> collectionField = new ArrayList<>();
////
////        public class InnerClass {
////            private boolean booleanInnerField = true;
////            private char charInnerField = 'a';
////            private byte byteInnerField = 1;
////            private File complexInnerField = new File("");
////        }
////    }
////
////    @Recordable(inclusionMode = InclusionMode.EXCLUDE_ALL)
////    public static class AnnotatedWithExcludeAll_FieldsAnnotatedWithIncluded_RecordableFixture {
////        @DataPoint(include = true) private boolean booleanField = true;
////        @DataPoint(include = true) private char charField = 'a';
////        @DataPoint(include = true) private byte byteField = 1;
////        @DataPoint(include = true) private short shortField = 2;
////        @DataPoint(include = true) private int intField = 3;
////        @DataPoint(include = true) private long longField = 4;
////        @DataPoint(include = true) private float floatField = 5.1f;
////        @DataPoint(include = true) private double doubleField = 6.1;
////
////        @DataPoint(include = true) private Boolean booleanWrapperField = false;
////        @DataPoint(include = true) private Character charWrapperField = 'b';
////        @DataPoint(include = true) private Byte byteWrapperField = 11;
////        @DataPoint(include = true) private Short shortWrapperField = 22;
////        @DataPoint(include = true) private Integer intWrapperField = 33;
////        @DataPoint(include = true) private Long longWrapperField = 44L;
////        @DataPoint(include = true) private Float floatWrapperField = 55.1f;
////        @DataPoint(include = true) private Double doubleWrapperField = 66.1;
////
////        @DataPoint(include = true) private String stringField = "hello";
////        @DataPoint(include = true) private State enumField = State.NEW;
//////        @DataPoint(include = true) private ComplexObjectFixture complexField = new ComplexObjectFixture();
//////        @DataPoint(include = true) private InnerClass innerClassField = new InnerClass();
////        @DataPoint(include = true) private Optional<Long> optionalOfRecordableField = Optional.of(123L);
//////        @DataPoint(include = true) private Optional<ComplexObjectFixture> optionalOfNonRecordableField = Optional.of(new ComplexObjectFixture());
////
//////        @DataPoint(include = true) private int[] arrayField = new int[]{1, 2, 3};
//////        @DataPoint(include = true) private Collection<Integer> collectionField = new ArrayList<>();
////
////        public class InnerClass {
////            private boolean booleanInnerField = true;
////            private char charInnerField = 'a';
////            private byte byteInnerField = 1;
////            private File complexInnerField = new File("");
////        }
////    }
////
////    @Recordable(inclusionMode = InclusionMode.INCLUDE_ALL_FAIL_FAST)
////    public static class AnnotatedWithIncludeAllFailFast_FieldsAnnotatedWithIncluded_RecordableFixture {
////        @DataPoint(include = true) private boolean booleanField = true;
////        @DataPoint(include = true) private char charField = 'a';
////        @DataPoint(include = true) private byte byteField = 1;
////        @DataPoint(include = true) private short shortField = 2;
////        @DataPoint(include = true) private int intField = 3;
////        @DataPoint(include = true) private long longField = 4;
////        @DataPoint(include = true) private float floatField = 5.1f;
////        @DataPoint(include = true) private double doubleField = 6.1;
////
////        @DataPoint(include = true) private Boolean booleanWrapperField = false;
////        @DataPoint(include = true) private Character charWrapperField = 'b';
////        @DataPoint(include = true) private Byte byteWrapperField = 11;
////        @DataPoint(include = true) private Short shortWrapperField = 22;
////        @DataPoint(include = true) private Integer intWrapperField = 33;
////        @DataPoint(include = true) private Long longWrapperField = 44L;
////        @DataPoint(include = true) private Float floatWrapperField = 55.1f;
////        @DataPoint(include = true) private Double doubleWrapperField = 66.1;
////
////        @DataPoint(include = true) private String stringField = "hello";
////        @DataPoint(include = true) private State enumField = State.NEW;
//////        @DataPoint(include = true) private ComplexObjectFixture complexField = new ComplexObjectFixture();
//////        @DataPoint(include = true) private InnerClass innerClassField = new InnerClass();
////        @DataPoint(include = true) private Optional<Long> optionalOfRecordableField = Optional.of(123L);
//////        @DataPoint(include = true) private Optional<ComplexObjectFixture> optionalOfNonRecordableField = Optional.of(new ComplexObjectFixture());
////
//////        @DataPoint(include = true) private int[] arrayField = new int[]{1, 2, 3};
//////        @DataPoint(include = true) private Collection<Integer> collectionField = new ArrayList<>();
////
////        public class InnerClass {
////            private boolean booleanInnerField = true;
////            private char charInnerField = 'a';
////            private byte byteInnerField = 1;
////            private File complexInnerField = new File("");
////        }
////    }
////
////    //
////
////    public static class Basic_FieldsAnnotatedWithExcluded_RecordableFixture {
////        @DataPoint(include = false) private boolean booleanField = true;
////        @DataPoint(include = false) private char charField = 'a';
////        @DataPoint(include = false) private byte byteField = 1;
////        @DataPoint(include = false) private short shortField = 2;
////        @DataPoint(include = false) private int intField = 3;
////        @DataPoint(include = false) private long longField = 4;
////        @DataPoint(include = false) private float floatField = 5.1f;
////        @DataPoint(include = false) private double doubleField = 6.1;
////
////        @DataPoint(include = false) private Boolean booleanWrapperField = false;
////        @DataPoint(include = false) private Character charWrapperField = 'b';
////        @DataPoint(include = false) private Byte byteWrapperField = 11;
////        @DataPoint(include = false) private Short shortWrapperField = 22;
////        @DataPoint(include = false) private Integer intWrapperField = 33;
////        @DataPoint(include = false) private Long longWrapperField = 44L;
////        @DataPoint(include = false) private Float floatWrapperField = 55.1f;
////        @DataPoint(include = false) private Double doubleWrapperField = 66.1;
////
////        @DataPoint(include = false) private String stringField = "hello";
////        @DataPoint(include = false) private State enumField = State.NEW;
////        @DataPoint(include = false) private ComplexObjectFixture complexField = new ComplexObjectFixture();
////        @DataPoint(include = false) private InnerClass innerClassField = new InnerClass();
////        @DataPoint(include = false) private Optional<Long> optionalOfRecordableField = Optional.of(123L);
////        @DataPoint(include = false) private Optional<ComplexObjectFixture> optionalOfNonRecordableField = Optional.of(new ComplexObjectFixture());
////
////        @DataPoint(include = false) private int[] arrayField = new int[]{1, 2, 3};
////        @DataPoint(include = false) private Collection<Integer> collectionField = new ArrayList<>();
////
////        public class InnerClass {
////            private boolean booleanInnerField = true;
////            private char charInnerField = 'a';
////            private byte byteInnerField = 1;
////            private File complexInnerField = new File("");
////        }
////    }
////
////    @Recordable
////    public static class Annotated_FieldsAnnotatedWithExcluded_RecordableFixture {
////        @DataPoint(include = false) private boolean booleanField = true;
////        @DataPoint(include = false) private char charField = 'a';
////        @DataPoint(include = false) private byte byteField = 1;
////        @DataPoint(include = false) private short shortField = 2;
////        @DataPoint(include = false) private int intField = 3;
////        @DataPoint(include = false) private long longField = 4;
////        @DataPoint(include = false) private float floatField = 5.1f;
////        @DataPoint(include = false) private double doubleField = 6.1;
////
////        @DataPoint(include = false) private Boolean booleanWrapperField = false;
////        @DataPoint(include = false) private Character charWrapperField = 'b';
////        @DataPoint(include = false) private Byte byteWrapperField = 11;
////        @DataPoint(include = false) private Short shortWrapperField = 22;
////        @DataPoint(include = false) private Integer intWrapperField = 33;
////        @DataPoint(include = false) private Long longWrapperField = 44L;
////        @DataPoint(include = false) private Float floatWrapperField = 55.1f;
////        @DataPoint(include = false) private Double doubleWrapperField = 66.1;
////
////        @DataPoint(include = false) private String stringField = "hello";
////        @DataPoint(include = false) private State enumField = State.NEW;
////        @DataPoint(include = false) private ComplexObjectFixture complexField = new ComplexObjectFixture();
////        @DataPoint(include = false) private InnerClass innerClassField = new InnerClass();
////        @DataPoint(include = false) private Optional<Long> optionalOfRecordableField = Optional.of(123L);
////        @DataPoint(include = false) private Optional<ComplexObjectFixture> optionalOfNonRecordableField = Optional.of(new ComplexObjectFixture());
////
////        @DataPoint(include = false) private int[] arrayField = new int[]{1, 2, 3};
////        @DataPoint(include = false) private Collection<Integer> collectionField = new ArrayList<>();
////
////        public class InnerClass {
////            private boolean booleanInnerField = true;
////            private char charInnerField = 'a';
////            private byte byteInnerField = 1;
////            private File complexInnerField = new File("");
////        }
////    }
////
////    @Recordable(inclusionMode = InclusionMode.INCLUDE_ALL)
////    public static class AnnotatedWithIncludeAll_FieldsAnnotatedWithExcluded_RecordableFixture {
////        @DataPoint(include = false) private boolean booleanField = true;
////        @DataPoint(include = false) private char charField = 'a';
////        @DataPoint(include = false) private byte byteField = 1;
////        @DataPoint(include = false) private short shortField = 2;
////        @DataPoint(include = false) private int intField = 3;
////        @DataPoint(include = false) private long longField = 4;
////        @DataPoint(include = false) private float floatField = 5.1f;
////        @DataPoint(include = false) private double doubleField = 6.1;
////
////        @DataPoint(include = false) private Boolean booleanWrapperField = false;
////        @DataPoint(include = false) private Character charWrapperField = 'b';
////        @DataPoint(include = false) private Byte byteWrapperField = 11;
////        @DataPoint(include = false) private Short shortWrapperField = 22;
////        @DataPoint(include = false) private Integer intWrapperField = 33;
////        @DataPoint(include = false) private Long longWrapperField = 44L;
////        @DataPoint(include = false) private Float floatWrapperField = 55.1f;
////        @DataPoint(include = false) private Double doubleWrapperField = 66.1;
////
////        @DataPoint(include = false) private String stringField = "hello";
////        @DataPoint(include = false) private State enumField = State.NEW;
////        @DataPoint(include = false) private ComplexObjectFixture complexField = new ComplexObjectFixture();
////        @DataPoint(include = false) private InnerClass innerClassField = new InnerClass();
////        @DataPoint(include = false) private Optional<Long> optionalOfRecordableField = Optional.of(123L);
////        @DataPoint(include = false) private Optional<ComplexObjectFixture> optionalOfNonRecordableField = Optional.of(new ComplexObjectFixture());
////
////        @DataPoint(include = false) private int[] arrayField = new int[]{1, 2, 3};
////        @DataPoint(include = false) private Collection<Integer> collectionField = new ArrayList<>();
////
////        public class InnerClass {
////            private boolean booleanInnerField = true;
////            private char charInnerField = 'a';
////            private byte byteInnerField = 1;
////            private File complexInnerField = new File("");
////        }
////    }
////
////    @Recordable(inclusionMode = InclusionMode.EXCLUDE_ALL)
////    public static class AnnotatedWithExcludeAll_FieldsAnnotatedWithExcluded_RecordableFixture {
////        @DataPoint(include = false) private boolean booleanField = true;
////        @DataPoint(include = false) private char charField = 'a';
////        @DataPoint(include = false) private byte byteField = 1;
////        @DataPoint(include = false) private short shortField = 2;
////        @DataPoint(include = false) private int intField = 3;
////        @DataPoint(include = false) private long longField = 4;
////        @DataPoint(include = false) private float floatField = 5.1f;
////        @DataPoint(include = false) private double doubleField = 6.1;
////
////        @DataPoint(include = false) private Boolean booleanWrapperField = false;
////        @DataPoint(include = false) private Character charWrapperField = 'b';
////        @DataPoint(include = false) private Byte byteWrapperField = 11;
////        @DataPoint(include = false) private Short shortWrapperField = 22;
////        @DataPoint(include = false) private Integer intWrapperField = 33;
////        @DataPoint(include = false) private Long longWrapperField = 44L;
////        @DataPoint(include = false) private Float floatWrapperField = 55.1f;
////        @DataPoint(include = false) private Double doubleWrapperField = 66.1;
////
////        @DataPoint(include = false) private String stringField = "hello";
////        @DataPoint(include = false) private State enumField = State.NEW;
////        @DataPoint(include = false) private ComplexObjectFixture complexField = new ComplexObjectFixture();
////        @DataPoint(include = false) private InnerClass innerClassField = new InnerClass();
////        @DataPoint(include = false) private Optional<Long> optionalOfRecordableField = Optional.of(123L);
////        @DataPoint(include = false) private Optional<ComplexObjectFixture> optionalOfNonRecordableField = Optional.of(new ComplexObjectFixture());
////
////        @DataPoint(include = false) private int[] arrayField = new int[]{1, 2, 3};
////        @DataPoint(include = false) private Collection<Integer> collectionField = new ArrayList<>();
////
////        public class InnerClass {
////            private boolean booleanInnerField = true;
////            private char charInnerField = 'a';
////            private byte byteInnerField = 1;
////            private File complexInnerField = new File("");
////        }
////    }
////
////    @Recordable(inclusionMode = InclusionMode.INCLUDE_ALL_FAIL_FAST)
////    public static class AnnotatedWithIncludeAllFailFast_FieldsAnnotatedWithExcluded_RecordableFixture {
////        @DataPoint(include = false) private boolean booleanField = true;
////        @DataPoint(include = false) private char charField = 'a';
////        @DataPoint(include = false) private byte byteField = 1;
////        @DataPoint(include = false) private short shortField = 2;
////        @DataPoint(include = false) private int intField = 3;
////        @DataPoint(include = false) private long longField = 4;
////        @DataPoint(include = false) private float floatField = 5.1f;
////        @DataPoint(include = false) private double doubleField = 6.1;
////
////        @DataPoint(include = false) private Boolean booleanWrapperField = false;
////        @DataPoint(include = false) private Character charWrapperField = 'b';
////        @DataPoint(include = false) private Byte byteWrapperField = 11;
////        @DataPoint(include = false) private Short shortWrapperField = 22;
////        @DataPoint(include = false) private Integer intWrapperField = 33;
////        @DataPoint(include = false) private Long longWrapperField = 44L;
////        @DataPoint(include = false) private Float floatWrapperField = 55.1f;
////        @DataPoint(include = false) private Double doubleWrapperField = 66.1;
////
////        @DataPoint(include = false) private String stringField = "hello";
////        @DataPoint(include = false) private State enumField = State.NEW;
////        @DataPoint(include = false) private ComplexObjectFixture complexField = new ComplexObjectFixture();
////        @DataPoint(include = false) private InnerClass innerClassField = new InnerClass();
////        @DataPoint(include = false) private Optional<Long> optionalOfRecordableField = Optional.of(123L);
////        @DataPoint(include = false) private Optional<ComplexObjectFixture> optionalOfNonRecordableField = Optional.of(new ComplexObjectFixture());
////
////        @DataPoint(include = false) private int[] arrayField = new int[]{1, 2, 3};
////        @DataPoint(include = false) private Collection<Integer> collectionField = new ArrayList<>();
////
////        public class InnerClass {
////            private boolean booleanInnerField = true;
////            private char charInnerField = 'a';
////            private byte byteInnerField = 1;
////            private File complexInnerField = new File("");
////        }
////    }
































//    private static final String INCLUDE_ALL_MODE = "GIVEN: INCLUDE_ALL_RECORDABLE mode - WHEN: Field is [";
//    private static final String EXCLUDE_ALL_MODE = "GIVEN: EXCLUDE_ALL mode - WHEN: Field is [";
//    private static final String FAIL_FAST_MODE = "GIVEN: INCLUDE_ALL_RECORDABLE_FAIL_FAST mode - WHEN: Field is [";
//    private static final String CREATE = "] - THEN: Create field mapping";
//    private static final String DONT_CREATE = "] - THEN: Don't create field mapping";
//    private static final String THROW_EXCEPTION = "] - THEN: Throw exception";



//    @Test
//    @DisplayName(INCLUDE_ALL_MODE + "primitive" + CREATE)
//    void primitive_Create() {
//        Object recordable = new PrimitiveOnly_RecordableFixture();
//
//        List<FieldMapping> fieldMappings = cleanMapper.createFieldMappings(recordable);
//
//        List<Class<?>> fieldClassList = fieldMappings.stream().map(it -> it.getField().getType()).collect(Collectors.toList());
//
//        assertThat(fieldClassList).contains(boolean.class);
//        assertThat(fieldClassList).contains(char.class);
//        assertThat(fieldClassList).contains(byte.class);
//        assertThat(fieldClassList).contains(short.class);
//        assertThat(fieldClassList).contains(int.class);
//        assertThat(fieldClassList).contains(long.class);
//        assertThat(fieldClassList).contains(float.class);
//        assertThat(fieldClassList).contains(double.class);
//    }
//
//    @Recordable(inclusionMode = InclusionMode.EXCLUDE_ALL)
//    public static class PrimitiveOnly_ExcludeAllMode_RecordableFixture implements LifecycleAwareRecordable {
//        private boolean booleanField = true;
//        private char charField = 'a';
//        private byte byteField = 1;
//        private short shortField = 2;
//        private int intField = 3;
//        private long longField = 4;
//        private float floatField = 5.1f;
//        private double doubleField = 6.1;
//    }
//
//    @Test
//    @DisplayName(EXCLUDE_ALL_MODE + "primitive" + DONT_CREATE)
//    void excludeAll_Primitive_DontCreate() {
//        Object recordable = new PrimitiveOnly_ExcludeAllMode_RecordableFixture();
//
//        List<FieldMapping> fieldMappings = cleanMapper.createFieldMappings(recordable);
//
//        List<Class<?>> fieldClassList = fieldMappings.stream().map(it -> it.getField().getType()).collect(Collectors.toList());
//
//        assertThat(fieldClassList).isEmpty();
//    }
//
//    @Recordable(inclusionMode = InclusionMode.EXCLUDE_ALL)
//    public static class PrimitiveOnly_WithDataPoint_ExcludeAllMode_RecordableFixture implements LifecycleAwareRecordable {
//        @DataPoint
//        private boolean booleanField = true;
//        @DataPoint(include = true)
//        private char charField = 'a';
//        @DataPoint(include = false)
//        private byte byteField = 1;
//        private short shortField = 2;
//        private int intField = 3;
//        private long longField = 4;
//        private float floatField = 5.1f;
//        private double doubleField = 6.1;
//    }
//
//    @Test
//    @DisplayName(EXCLUDE_ALL_MODE + "primitive annotated with @DataPoint" + CREATE)
//    void excludeAll_PrimitiveWithDataPoint_Create() {
//        Object recordable = new PrimitiveOnly_WithDataPoint_ExcludeAllMode_RecordableFixture();
//
//        List<FieldMapping> fieldMappings = cleanMapper.createFieldMappings(recordable);
//
//        List<Class<?>> fieldClassList = fieldMappings.stream().map(it -> it.getField().getType()).collect(Collectors.toList());
//
//        assertThat(fieldClassList).contains(boolean.class);
//        assertThat(fieldClassList).contains(char.class);
//        assertThat(fieldClassList).doesNotContain(byte.class);
//        assertThat(fieldClassList).doesNotContain(short.class);
//        assertThat(fieldClassList).doesNotContain(int.class);
//        assertThat(fieldClassList).doesNotContain(long.class);
//        assertThat(fieldClassList).doesNotContain(float.class);
//        assertThat(fieldClassList).doesNotContain(double.class);
//    }
//
//    public static class PrimitiveWrapperOnly_RecordableFixture implements LifecycleAwareRecordable {
//        private Boolean booleanField = true;
//        private Character charField = 'a';
//        private Byte byteField = 1;
//        private Short shortField = 2;
//        private Integer intField = 3;
//        private Long longField = 4L;
//        private Float floatField = 5.1f;
//        private Double doubleField = 6.1;
//    }
//
//    @Test
//    @DisplayName(INCLUDE_ALL_MODE + "primitive wrapper" + CREATE)
//    void primitiveWrapper_Create() {
//        Object recordable = new PrimitiveWrapperOnly_RecordableFixture();
//
//        List<FieldMapping> fieldMappings = cleanMapper.createFieldMappings(recordable);
//
//        List<Class<?>> fieldClassList = fieldMappings.stream().map(it -> it.getField().getType()).collect(Collectors.toList());
//
//        assertThat(fieldClassList).contains(Boolean.class);
//        assertThat(fieldClassList).contains(Character.class);
//        assertThat(fieldClassList).contains(Byte.class);
//        assertThat(fieldClassList).contains(Short.class);
//        assertThat(fieldClassList).contains(Integer.class);
//        assertThat(fieldClassList).contains(Long.class);
//        assertThat(fieldClassList).contains(Float.class);
//        assertThat(fieldClassList).contains(Double.class);
//    }
//
//    @Recordable(inclusionMode = InclusionMode.EXCLUDE_ALL)
//    public static class PrimitiveWrapperOnly_ExcludeAllMode_RecordableFixture implements LifecycleAwareRecordable {
//        private Boolean booleanField = true;
//        private Character charField = 'a';
//        private Byte byteField = 1;
//        private Short shortField = 2;
//        private Integer intField = 3;
//        private Long longField = 4L;
//        private Float floatField = 5.1f;
//        private Double doubleField = 6.1;
//    }
//
//    @Test
//    @DisplayName(EXCLUDE_ALL_MODE + "primitive wrapper" + DONT_CREATE)
//    void excludeAll_PrimitiveWrapper_DontCreate() {
//        Object recordable = new PrimitiveWrapperOnly_ExcludeAllMode_RecordableFixture();
//
//        List<FieldMapping> fieldMappings = cleanMapper.createFieldMappings(recordable);
//
//        List<Class<?>> fieldClassList = fieldMappings.stream().map(it -> it.getField().getType()).collect(Collectors.toList());
//
//        assertThat(fieldClassList).isEmpty();
//    }
//
//    @Recordable(inclusionMode = InclusionMode.EXCLUDE_ALL)
//    public static class PrimitiveWrapperOnly_WithDataPoint_ExcludeAllMode_RecordableFixture implements LifecycleAwareRecordable {
//        @DataPoint
//        private Boolean booleanField = true;
//        @DataPoint(include = true)
//        private Character charField = 'a';
//        @DataPoint(include = false)
//        private Byte byteField = 1;
//        private Short shortField = 2;
//        private Integer intField = 3;
//        private Long longField = 4L;
//        private Float floatField = 5.1f;
//        private Double doubleField = 6.1;
//    }
//
//    @Test
//    @DisplayName(EXCLUDE_ALL_MODE + "primitive wrapper annotated with @DataPoint" + CREATE)
//    void excludeAll_PrimitiveWrapperWithDataPoint_Create() {
//        Object recordable = new PrimitiveWrapperOnly_WithDataPoint_ExcludeAllMode_RecordableFixture();
//
//        List<FieldMapping> fieldMappings = cleanMapper.createFieldMappings(recordable);
//
//        List<Class<?>> fieldClassList = fieldMappings.stream().map(it -> it.getField().getType()).collect(Collectors.toList());
//
//        assertThat(fieldClassList).contains(Boolean.class);
//        assertThat(fieldClassList).contains(Character.class);
//        assertThat(fieldClassList).doesNotContain(Byte.class);
//        assertThat(fieldClassList).doesNotContain(Short.class);
//        assertThat(fieldClassList).doesNotContain(Integer.class);
//        assertThat(fieldClassList).doesNotContain(Long.class);
//        assertThat(fieldClassList).doesNotContain(Float.class);
//        assertThat(fieldClassList).doesNotContain(Double.class);
//    }
//
//    public static class String_RecordableFixture implements LifecycleAwareRecordable {
//        private String stringField = "hello";
//    }
//
//    @Test
//    @DisplayName(INCLUDE_ALL_MODE + "String" + CREATE)
//    void string_Create() {
//        Object recordable = new String_RecordableFixture();
//
//        List<FieldMapping> fieldMappings = cleanMapper.createFieldMappings(recordable);
//
//        List<Class<?>> fieldClassList = fieldMappings.stream().map(it -> it.getField().getType()).collect(Collectors.toList());
//
//        assertThat(fieldClassList).containsExactly(String.class);
//    }
//
//    @Recordable(inclusionMode = InclusionMode.EXCLUDE_ALL)
//    public static class String_ExcludeAllMode_RecordableFixture implements LifecycleAwareRecordable {
//        private String stringField = "hello";
//    }
//
//    @Test
//    @DisplayName(EXCLUDE_ALL_MODE + "String" + DONT_CREATE)
//    void excludeAll_String_DontCreate() {
//        Object recordable = new String_ExcludeAllMode_RecordableFixture();
//
//        List<FieldMapping> fieldMappings = cleanMapper.createFieldMappings(recordable);
//
//        List<Class<?>> fieldClassList = fieldMappings.stream().map(it -> it.getField().getType()).collect(Collectors.toList());
//
//        assertThat(fieldClassList).isEmpty();
//    }
//
//    @Recordable(inclusionMode = InclusionMode.EXCLUDE_ALL)
//    public static class String_WithDataPoint_ExcludeAllMode_RecordableFixture implements LifecycleAwareRecordable {
//        @DataPoint
//        private String stringField1 = "hello";
//        @DataPoint(include = true)
//        private String stringField2 = "hello";
//        @DataPoint(include = false)
//        private String stringField3 = "hello";
//    }
//
//    @Test
//    @DisplayName(EXCLUDE_ALL_MODE + "String annotated with @DataPoint" + CREATE)
//    void excludeAll_StringWithDataPoint_Create() {
//        Object recordable = new String_WithDataPoint_ExcludeAllMode_RecordableFixture();
//
//        List<FieldMapping> fieldMappings = cleanMapper.createFieldMappings(recordable);
//
//        List<Class<?>> fieldClassList = fieldMappings.stream().map(it -> it.getField().getType()).collect(Collectors.toList());
//
//        assertThat(fieldClassList).containsExactly(String.class, String.class);
//    }
//
//    public static class Enum_RecordableFixture implements LifecycleAwareRecordable {
//        private State enumField = State.NEW;
//    }
//
//    @Test
//    @DisplayName(INCLUDE_ALL_MODE + "Enum" + CREATE)
//    void enum_Create() {
//        Object recordable = new Enum_RecordableFixture();
//
//        List<FieldMapping> fieldMappings = cleanMapper.createFieldMappings(recordable);
//
//        List<Class<?>> fieldClassList = fieldMappings.stream().map(it -> it.getField().getType()).collect(Collectors.toList());
//
//        assertThat(fieldClassList).containsExactly(Enum.class);
//    }
//
//    @Recordable(inclusionMode = InclusionMode.EXCLUDE_ALL)
//    public static class Enum_RecordableFixture implements LifecycleAwareRecordable {
//        private State enumField = State.NEW;
//    }
//
//    @Test
//    @DisplayName(EXCLUDE_ALL_MODE + "Enum" + DONT_CREATE)
//    void excludeAll_Enum_DontCreate() {
//        Throw.notImplemented("Test not implemented.");
//    }
//
//    @Test
//    @DisplayName(EXCLUDE_ALL_MODE + "Enum annotated with @DataPoint" + CREATE)
//    void excludeAll_EnumWithDataPoint_Create() {
//        Throw.notImplemented("Test not implemented.");
//    }
//
//    @Test
//    @DisplayName(INCLUDE_ALL_MODE + "Optional holding recordable value" + CREATE)
//    void optionalHoldingRecordable_Create() {
//        Throw.notImplemented("Test not implemented.");
//    }
//
//    @Test
//    @DisplayName(EXCLUDE_ALL_MODE + "Optional holding recordable value" + DONT_CREATE)
//    void excludeAll_OptionalHoldingRecordable_DontCreate() {
//        Throw.notImplemented("Test not implemented.");
//    }
//
//    @Test
//    @DisplayName(EXCLUDE_ALL_MODE + "Optional holding recordable value annotated with @DataPoint" + CREATE)
//    void excludeAll_OptionalHoldingRecordableWithDataPoint_Create() {
//        Throw.notImplemented("Test not implemented.");
//    }
//
//    @Test
//    @DisplayName(INCLUDE_ALL_MODE + "Optional holding non-recordable value" + DONT_CREATE)
//    void optionalHoldingNonRecordable_DontCreate() {
//        Throw.notImplemented("Test not implemented.");
//    }
//
//    @Test
//    @DisplayName(FAIL_FAST_MODE + "Optional holding non-recordable value" + THROW_EXCEPTION)
//    void optionalHoldingNonRecordable_ThrowException() {
//        Throw.notImplemented("Test not implemented.");
//    }
//
//    @Test
//    @DisplayName(INCLUDE_ALL_MODE + "recordable complex object" + CREATE)
//    void recordableComplexObject_Create() {
//        Throw.notImplemented("Test not implemented.");
//        // Annotated with @Recordable
//        // Defined in containing class as @Recordable(additionalIncludes={"fieldName"})
//        // Configuration passed in
//    }
//
//    @Test
//    @DisplayName(EXCLUDE_ALL_MODE + "recordable complex object" + DONT_CREATE)
//    void excludeAll_RecordabledComplexObject_DontCreate() {
//        Throw.notImplemented("Test not implemented.");
//    }
//
//    @Test
//    @DisplayName(EXCLUDE_ALL_MODE + "recordable complex object annotated with @DataPoint" + CREATE)
//    void excludeAll_RecordabledComplexObjectWithDataPoint_Create() {
//        Throw.notImplemented("Test not implemented.");
//    }
//
//    @Test
//    @DisplayName(INCLUDE_ALL_MODE + "non-recordable complex object" + DONT_CREATE)
//    void nonRecordableComplexObject_DontCreate() {
//        Throw.notImplemented("Test not implemented.");
//    }
//
//    @Test
//    @DisplayName(FAIL_FAST_MODE + "non-recordable complex object" + THROW_EXCEPTION)
//    void nonRecordableComplexObject_ThrowException() {
//        Throw.notImplemented("Test not implemented.");
//    }
//
//    @Test
//    @DisplayName(INCLUDE_ALL_MODE + "array" + DONT_CREATE)
//    void array_DontCreate() {
//        Throw.notImplemented("Test not implemented.");
//    }
//
//    @Test
//    @DisplayName(FAIL_FAST_MODE + "array" + THROW_EXCEPTION)
//    void array_ThrowException() {
//        Throw.notImplemented("Test not implemented.");
//    }
//
//    @Test
//    @DisplayName(INCLUDE_ALL_MODE + "Collection" + DONT_CREATE)
//    void collection_DontCreate() {
//        Throw.notImplemented("Test not implemented.");
//    }
//
//    @Test
//    @DisplayName(FAIL_FAST_MODE + "Collection" + THROW_EXCEPTION)
//    void collection_ThrowException() {
//        Throw.notImplemented("Test not implemented.");
//    }
//
//    @Test
//    @DisplayName(INCLUDE_ALL_MODE + "final" + DONT_CREATE)
//    void final_DontCreate() {
//        Throw.notImplemented("Test not implemented.");
//    }
//
//    @Test
//    @DisplayName(FAIL_FAST_MODE + "final" + THROW_EXCEPTION)
//    void final_ThrowException() {
//        Throw.notImplemented("Test not implemented.");
//    }
//
//    @Test
//    @DisplayName(INCLUDE_ALL_MODE + "final annotated with @DataPoint" + CREATE)
//    void finalWithDataPoint_Create() {
//        Throw.notImplemented("Test not implemented.");
//    }
//
//    @Test
//    @DisplayName(INCLUDE_ALL_MODE + "static" + DONT_CREATE)
//    void static_DontCreate() {
//        Throw.notImplemented("Test not implemented.");
//    }
//
//    @Test
//    @DisplayName(FAIL_FAST_MODE + "static" + THROW_EXCEPTION)
//    void static_ThrowException() {
//        Throw.notImplemented("Test not implemented.");
//    }
//
//    @Test
//    @DisplayName(INCLUDE_ALL_MODE + "static annotated with @DataPoint" + CREATE)
//    void staticWithDataPoint_Create() {
//        Throw.notImplemented("Test not implemented.");
//    }
//
//    @Test
//    @DisplayName(INCLUDE_ALL_MODE + "annotated with @DataPointIgnore" + DONT_CREATE)
//    void dataPointIgnore_DontCreate() {
//        Throw.notImplemented("Test not implemented.");
//    }
//
//    @Test
//    @DisplayName(FAIL_FAST_MODE + "any non-recordable annotated with @DataPointIgnore" + DONT_CREATE)
//    void nonRecordableWithDataPointIgnore_DontCreate() {
//        Throw.notImplemented("Test not implemented.");
//    }
//
//    // Inner class
//
//
//
//    // ============================================================
//    // Other
//    // ============================================================
//
//    @Test
//    @DisplayName("WHEN: name attribute of @DataPoint specified - THEN: Use name provided")
//    void nameAttribute_UseName() {
//        Throw.notImplemented("Test not implemented.");
//    }
//
//    @Test
//    void GIVEN_IncludeAllRecordableMode_WHEN_FieldIsNotPrimitive_THEN_DontCreateFieldMapping() {
//        Throw.notImplemented("Test not implemented.);
//
//    }
//
//    @Test
//    void GIVEN_IncludeAllRecordableFailFastMode_WHEN_FieldIsNotPrimitive_THEN_ThrowException() {
//        Throw.notImplemented("Test not implemented.);
//
//    }
//
//    @Test
//    void GIVEN_IncludeAllRecordableMode_WHEN_FieldIsNotPrimitive_THEN_DontCreateFieldMapping() {
//        Throw.notImplemented("Test not implemented.);
//
//    }


}
