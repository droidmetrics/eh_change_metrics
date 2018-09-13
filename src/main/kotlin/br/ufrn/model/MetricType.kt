package br.ufrn.model


enum class MetricType {

    /**
     * VERSION N-1
     *
     * public class C1 {
     *  public void M1() throws E2 {
     *      try {
     *          M7();
     *      } catch (E1) {
     *          LogA();
     *      }
     *  }
     * }
     *
     * VERSION N
     * public class C1 {
     *  public void M1() throws E3 {
     *      try {
     *          M3();
     *      } catch (E1) {
     *          LogB();
     *      }
     *  }
     *  public void M2() throws E3, E2 {
     *      try {
     *          M4();
     *      } catch (E1) {
     *          LogB();
     *      }
     *  }
     * }
     *
     * */

    /**
     * It counts the number of lines of exceptional code added or changed between a pair of versions.
     * In the example presented in Figure 1, four lines of exceptional code (one method interface and one catch block)
     * are added into method M2 and two lines are changed (one interface changed and one catch block changed) in method M1.
     * Hence, the value of the EH-ChurnedLOC metric is 6.
     * */
    EHChurnedLOC,

    /**
     * They count, respectively, the number of meth- ods that added, changed or removed exceptional interfaces between
     * a pair of versions. In the example presented in Figure 1, one interface is added and one is changed. Hence, the
     * value of InterfaceAdded is 1 and the value of InterfaceChanged is 1.
     * */
    InterfaceAdded,
    InterfaceChanged,
    InterfaceRemoved,

    /**
     * These metrics count, respectively, the number of exception types that were added, changed, or removed from
     * exceptional interfaces. In the example presented in Figure 1, one exception type is changed and two are added.
     * Hence, the value of InterfaceTypeChanged is 1 and the value of InterfaceTypeAdded is 2
     * */
    InterfaceTypeAdded,
    InterfaceTypeChanged,
    InterfaceTypeRemoved,

    /**
     * It counts the number of classes that have an exception interface or catch block added, changed, or removed
     * between a pair of versions. In the example presented in Figure 1, catch blocks and interfaces were added
     * and changed in the same class, so the value of ClassChurned is 1.
     * */
    ClassChurned,

    /**
     * It counts the number of methods that have an interface or catch block added, changed, or removed between a
     * pair of versions. In the example presented in Figure 1, interfaces and catch blocks were added and changed
     * in two different methods, so the value of MethodChurned is 2.
     * */
    MethodChurned,

    /**
     * It counts the sum of the added and changed lines of normal code for a pair of versions. In the example presented
     * in Figure 1, one line of nor- mal code is changed (within the first try block) and four lines are added (one line
     * for the method signature and three lines for the try block). Hence, the value of the NormalChurnedLOC is 5. We
     * employed the Git to count NomalChurnedLOC. Git provides the Churned- LOC metric, which counts the sum of the added
     * or changed lines without distinguishing whether the code is normal or exceptional. For that reason, we subtract the
     * absolute value of the ChurnedLOC metric provided by the tool from the sum of EHChurnedLOC, e.g.,
     * NormalChurnedLOC = ChurnedLOC - EHChurnedLOC.
     * */
    NormalChurnedLOC;
}