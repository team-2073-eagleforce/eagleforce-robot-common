package com.team2073.common.proploader;

/**
 * @author Preston Briggs
 */
public class PropertyLoaderTestFixtures {

    public static class SimpleProperties {
        private String foo;
        private int numb;

        public String getFoo() {
            return foo;
        }

        public void setFoo(String foo) {
            this.foo = foo;
        }

        public int getNumb() {
            return numb;
        }

        public void setNumb(int numb) {
            this.numb = numb;
        }
    }
}
