
/**
 * Created with IntelliJ IDEA.
 * User: Greene
 * Date: 6/27/12
 * Time: 2:50 PM
 * To change this template use File | Settings | File Templates.
 */
class FizzBuzzKata extends GroovyTestCase
{
    def fizzBuzz()
    {
        (1..100).each
        {
            def output = ""
            def div3 = it % 3 == 0
            def div5 = it % 5 == 0
            if (div3) { output += "Fizz" }
            if (div5) { output += "Buzz" }
            if (!div3 && !div5) { output = it }
            println output
        }
        return true
    }

    void test1()
    {
        assert fizzBuzz()
    }
}
