package ist.meic.pa.tests;


import ist.meic.pa.tests.domain.Com;

public class TestD {
    public static void main(String[] args) {
        Object objects = new Object[]{"Foo", new Integer[]{123, -12}};
        System.out.println(Com.bine(new Object[]{objects}));

        Object numbers = new Object[]{123, new Integer[]{456, 21}};
        System.out.println(Com.bine(new Object[]{numbers}));
    }
}
