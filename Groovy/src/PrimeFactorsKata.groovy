
/**
 * Created with IntelliJ IDEA.
 * User: Greene
 * Date: 6/26/12
 * Time: 3:36 PM
 * To change this template use File | Settings | File Templates.
 */

class PrimeFactorsKata extends GroovyTestCase
{
    def primeFactors(i)
    {
        def result = []
        def factor = 2

        while ( i > 1 )
        {
            if ( ( i % factor ) == 0 )
            {
                result.push( factor )
                i = ( i / factor ).toInteger()
            }
            else
            {
                ++factor
            }
        }

        return result
    }

    void test1()
    {
        assertEquals( [], primeFactors(1) )
    }

    void test2()
    {
        assertEquals( [2], primeFactors(2) )
    }

    void test3()
    {
        assertEquals( [3], primeFactors(3) )
    }

    void test4()
    {
        assertEquals( [2,2], primeFactors(4) )
    }

    void test5()
    {
        assertEquals( [2,3], primeFactors(6) )
    }

    void test6()
    {
        assertEquals( [2,2,2], primeFactors(8) )
    }

    void test7()
    {
        assertEquals( [3,3], primeFactors(9) )
    }
}