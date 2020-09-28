package pkg.caller.test;

/**
 * @author c7ch23en
 */
public class TestCaller {

    public void call(Runnable task) {
        task.run();
    }

}
