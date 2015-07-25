
/**
 * Created with IntelliJ IDEA.
 * User: mark
 * Date: 6/28/12
 * Time: 7:36 PM
 * http://codingdojo.org/cgi-bin/wiki.pl?KataBankOCR
 */
class BankOCRKata extends GroovyTestCase
{

   final def ZERO =  " _ " +
                     "| |" +
                     "|_|"

   final def ONE =   "   " +
                     "  |" +
                     "  |"

   final def TWO =   " _ " +
                     " _|" +
                     "|_ "

   final def THREE = " _ " +
                     " _|" +
                     " _|"

   final def FOUR =  "   " +
                     "|_|" +
                     "  |"

   final def FIVE =  " _ " +
                     "|_ " +
                     " _|"

   final def SIX =   " _ " +
                     "|_ " +
                     "|_|"

   final def SEVEN = " _ " +
                     "  |" +
                     "  |"

   final def EIGHT = " _ " +
                     "|_|" +
                     "|_|"

   final def NINE =  " _ " +
                     "|_|" +
                     " _|"

   final def NUMBERS = [ ZERO, ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE ]


   // account number:  3  4  5  8  8  2  8  6  5
   // position names: n9 n8 n7 n6 n5 n4 n3 n2 n1
   //
   // (1*n1 + 2*n2 + 3*n3 + 4*n4 + 5*n5 + 6*n6 + 7*n7 + 8*n8 + 9*n9) mod 11 = 0
   def checkAccount = { number ->
      def sum = 0
      (number.size()..1).eachWithIndex { pos, f ->
         sum += (f + 1) * number[pos - 1].toInteger()
      }
      sum % 11 == 0
   }


   void bankOCR( name, format = false, fix = false )
   {

      new File(name).withReader
            { reader ->
               // Read file 4 lines at a time
               def line1, line2, line3
               while (  (line1 = reader.readLine()) != null &&
                        (line2 = reader.readLine()) != null &&
                        (line3 = reader.readLine()) != null &&
                        reader.readLine() != null )
               {
                  // Read lines 3x3 characters at a time
                  def ocr = readOCR(line1, line2, line3)

                  println line1
                  println line2
                  println line3
                  println ""
                  def accounts = scanOCR(ocr, fix)
                  println "=> " + formatResults(accounts, format)
               }
            }

   }


   def readOCR( line1, line2, line3 )
   {
      // Read 3x3 characters
      def ocr = []

      for ( i in [0, 3, 6, 9, 12, 15, 18, 21, 24] )
      {
         def n = line1.substring(i, i+3) + line2.substring(i, i+3) + line3.substring(i, i+3)
         ocr.add(n)
      }

      ocr
   }


   def scanOCR( ocr, fix = true )
   {
      // Scan for account number(s)
      def scan = []

      ocr.each
            {
               def number = findOCR(it, fix)
               scan += [number]
            }

      println "Scan => ${scan}:${scan.size()}"

      def accounts = [:]
      accounts.account = []
      def count = 0

      /*
         scan => [[1] [2,3] [4] [5,6,7] [8,9]]
                   ^
                  pos

          for each position...
          accounts => [12458, 13458, 12468, 12478, 12459]
       */
      scan.eachWithIndex
         {  number, position ->
            // For each position

            // only one change allowed per account number (cant use scan.combinations())
            // number - the numbers (change set) for this position
            // change - the index of the number to change
            // count - first time through, include 0 else start at 1 to prevent duplicates
            //    (skips if there is only 1 number at position)
            for ( def change = count; change < number.size(); change++ )
            {
               // Generate all possible account numbers for the current position

               def account = ""
               scan.eachWithIndex
                  {  it, index ->
                     if (index == position)
                     {
                        account += it[change]
                     }
                     else
                     {
                        account += it[0]
                     }
                  }

               // save first account, valid or not
               if (!accounts.original) accounts.original = account;

               // if fix add only valid accounts
               if (fix && !account.contains("?") && checkAccount(account))
                  accounts.account += [account]
               else if (!fix)
                  accounts.account += [account]
            }
            if ( count == 0 ) count++
         }

      println "Accounts => ${accounts.original}, ${accounts.account}:${accounts.account.size()}"

      accounts
   }


   def findOCR( ocr, fix = true )
   {
      def number = []
      def value = -1

      // see if we are starting with a valid number
      if ( (value = NUMBERS.indexOf( ocr.toString()) ) != -1)
      {
         // println "Found => '${ocr}'"
         number += value.toString()
      }
      else
      {
         // println "Not Found => '${ocr}'"
         number += ["?"]
      }

      if ( fix )
      {
         // look for (other) valid number(s)
         def test = new StringBuilder( ocr.toString() )
         ocr.eachWithIndex()
            {  it, i ->
               def hold = it
               if (it == " ")
               {
                  // see if a '_' gives us a valid number
                  test[i..i] = "_"
                  if ( (value = NUMBERS.indexOf(test.toString())) != -1)
                  {
                     // println "Fixed with ' ':${i} => '${fix}'"
                     number += value.toString()
                  }
                  else
                  {
                     // see if a '|' gives us a valid number
                     test[i..i] = "|"
                     if ( (value = NUMBERS.indexOf(test.toString())) != -1)
                     {
                        // println "Fixed with '|':${i} => '${fix}'"
                        number += value.toString()
                     }
                  }
               }
               else
               {
                  // see if a ' ' gives us a valid number
                  test[i..i] = " "
                  if ( (value = NUMBERS.indexOf(test.toString())) != -1)
                  {
                     // println "Fixed with ' ':${i} => '${fix}'"
                     number += value.toString()
                  }
               }
               // put the character back and try again
               test[i..i] = hold
            }
      }

      number
   }

   
   def formatResults( accounts, format = true )
   {
      def results = ""

      def account = accounts.account
      if ( !account ) account = [accounts.original]

      // generate the output string to spec
      account.eachWithIndex
            {  number, index ->

               if ( format )
               {
                  if ( number.contains("?") )
                  {
                     results += number.toString() + " ILL"
                  }
                  else if ( !checkAccount(number) )
                  {
                     results += number.toString() + " ERR"
                  }
                  else if ( index == 0 && account.size() > 1 )
                  {
                     results += accounts.original + " AMB [ '" + number.toString() + "', '"
                  }
                  else if ( index == account.size()-1 && account.size() > 1)
                  {
                     results += number.toString() + "' ]"
                  }
                  else if ( account.size() > 1)
                  {
                     results += number.toString() + "', '"
                  }
                  else
                  {
                     results += number.toString()
                  }
               }
               else
               {
                  results += number.toString()
               }

            }

      results
   }


   void test1()
   {
      assert [ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO] == readOCR(
            " _  _  _  _  _  _  _  _  _ ",
            "| || || || || || || || || |",
            "|_||_||_||_||_||_||_||_||_|")
      assert [ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE] == readOCR(
            "    _  _     _  _  _  _  _ ",
            "  | _| _||_||_ |_   ||_||_|",
            "  ||_  _|  | _||_|  ||_| _|")
      assert [NINE, EIGHT, SEVEN, SIX, FIVE, FOUR, THREE, TWO, ONE] == readOCR(
            " _  _  _  _  _     _  _    ",
            "|_||_|  ||_ |_ |_| _| _|  |",
            " _||_|  ||_| _|  | _||_   |")
      println ""
      println "readOCR passed"
   }

   void test2()
   {
      assert ["0", "8"] == findOCR(
               " _ " +
               "| |" +
               "|_|")

      assert ["?", "0"] == findOCR(
               "   " +
               "| |" +
               "|_|")

      assert ["?", "0"] == findOCR(
               " _ " +
               "| |" +
               "| |")

      assert ["?", "0"] == findOCR(
               " _ " +
               "  |" +
               "|_|")

      assert ["?", "0"] == findOCR(
               " _ " +
               "| |" +
               "|_ ")

      assert ["8", "0", "6", "9"] == findOCR(
               " _ " +
               "|_|" +
               "|_|")

      assert ["?"] == findOCR(
               " _ " +
               " _ " +
               " _ ")

      println ""
      println "findOCR passed"
   }

   void test3()
   {
      assert checkAccount("123456789")

      assert checkAccount("457508000")
      assert !checkAccount("664371495")

      assert checkAccount("490867715")
      assert !checkAccount("888888888")
      println ""
      println "checkAccount passed"
   }

   void test4()
   {
      println ""
      assert ["0123456789"] == scanOCR( [ZERO, ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE], false ).account
      assert ["9876543210"] == scanOCR( [NINE, EIGHT, SEVEN, SIX, FIVE, FOUR, THREE, TWO, ONE, ZERO], false ).account
      assert ["0000000000"] == scanOCR( [ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO], false ).account

      assert ["457508000"] == scanOCR( [FOUR, FIVE, SEVEN, FIVE, ZERO, EIGHT, ZERO, ZERO, ZERO], false ).account
      assert ["1234?678?"] == scanOCR( [ONE, TWO, THREE, FOUR, " -  -  - ", SIX, SEVEN, EIGHT, " -  -  - "], false ).account
      assert ["664371495"] == scanOCR( [SIX, SIX, FOUR, THREE, SEVEN, ONE, FOUR, NINE, FIVE], false ).account

      assert "1234?678?" == scanOCR( [ONE, TWO, THREE, FOUR, " -  -  - ", SIX, SEVEN, EIGHT, " -  -  - "] ).original

      assert ["490867715"] == scanOCR( [FOUR, NINE, ZERO, EIGHT, SIX, SEVEN, SEVEN, ONE, FIVE] ).account
      assert ["888886888", "888888988", "888888880"] == scanOCR( [EIGHT, EIGHT, EIGHT, EIGHT, EIGHT, EIGHT, EIGHT, EIGHT, EIGHT] ).account

      println ""
      println "scanOCR passed"
   }

   void test5()
   {
      assert "457508000" == formatResults( [ original:"457508000", account: ["457508000"] ] )
      assert "1234?678? ILL" == formatResults( [ original:"1234?678?", account: ["1234?678?"] ] )
      assert "664371495 ERR" == formatResults( [ original:"664371495", account: ["664371495"] ] )

      assert "1234?678? ILL" == formatResults( [ original:"1234?678?", account: [] ] )

      assert "490867715" == formatResults( [ original:"490867715", account: ["490867715"] ] )
      assert "888888888 AMB [ '888886888', '888888988', '888888880' ]" == formatResults( [ original:"888888888", account: ["888886888", "888888988", "888888880"] ] )

      println ""
      println "formatResults passed"
   }

   void test6()
   {
      println ""
      println "Use Case 1"
      bankOCR("usecase1.dat")
      println ""
      println "Use Case 3"
      bankOCR("usecase3.dat", true)
      println ""
      println "Use Case 4"
      bankOCR("usecase4.dat", true, true)
      println ""
      println "bankOCR passed"
   }
}
