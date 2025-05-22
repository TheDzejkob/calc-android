package com.example.calculatorukol
import android.view.View
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.math.BigInteger

var firstNum:String? = "";
var operator:String? = "";
var secondNum:String? = "";
// kvuli int overflow při 16! a víš (ani Long nestačí big integer se dinamicky zvětšuje) nini zvlasdne tolik faktorialu kolik utahne procak telefonu
var result:BigInteger? = null;
var resolve:Boolean? = false;
var isOperator:Boolean? = false;
var wantSecondNum:Boolean? = false;


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    fun onClick(view: View) {

        var tag = view.tag.toString()
            val textView = findViewById<TextView>(R.id.AnswearDisplay)
            textView.text = tag;
        if (tag == "="){
            resolve = true;
            isOperator = false;
            updateDisplay();
        }
        else if (tag == "C"){
            firstNum = null;
            operator = "";
            secondNum = null;
            result = null;
            wantSecondNum = false;
            updateDisplay()
        }
        else if (tag == "!"){
            if (firstNum != null) {
                var num = firstNum!!.toInt();
                calcFactorial(num);
                resolve = true;
                updateDisplay();
            }
        }
        else if (tag == "del"){
            if(wantSecondNum == false){
                val tempFirstNum = firstNum
                if (!tempFirstNum.isNullOrEmpty()) {
                    firstNum = tempFirstNum.dropLast(1)
                }

            }
            else{
                val tempSecondNum = secondNum
                if (!tempSecondNum.isNullOrEmpty()) {
                    secondNum = tempSecondNum.dropLast(1)
                }
            }
            updateDisplay()
        }
        else if (tag.matches(Regex("\\d+"))) {
            firstNum += tag;
            updateDisplay();
        }


        // pokud je to čislo prida se do variable a updatne display pokud ne je to znamenko a bere se jako rozhodovač
        // pote druhe cislo (pokud se nejedna o delete nebo faktorial)


    }
    fun updateDisplay() {
        val answearDisplay = findViewById<TextView>(R.id.AnswearDisplay)
        val ProblemDisplay = findViewById<TextView>(R.id.probleDisplay)


        if (resolve == true) {
            answearDisplay.text = result.toString();
            resolve = false;
        }
        else{
            if (wantSecondNum == false) {
                ProblemDisplay.text = firstNum;
            }

            answearDisplay.text = "";
        }





    }

    fun calcFactorial(value: Int) {
        if (value < 0) {
            result = null // Factorial nemuže byt z negativniho (jsem trouba a debugoval to hoďku proč to děla :D)
            return
        }

        var fact = BigInteger.ONE
        for (i in 1..value) {
            fact = fact.multiply(BigInteger.valueOf(i.toLong()))
        }

        result = fact
    }

}