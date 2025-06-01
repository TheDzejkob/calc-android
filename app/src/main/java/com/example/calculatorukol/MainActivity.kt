package com.example.calculatorukol;
import android.view.View;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.enableEdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.math.BigInteger;
import android.media.MediaPlayer;

var firstNum: String = "";
var operator: String = "";
var secondNum: String = "";
var result: BigInteger? = null; // BigInteger pro podporu velkých čísel (alokuje se dynamicky lolísek :P)
var resolve: Boolean = false;
var isOperator: Boolean = false;
var wantSecondNum: Boolean = false;

class MainActivity : AppCompatActivity() {
    private var buttonSound: MediaPlayer? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        enableEdgeToEdge();

        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            insets;
        };
        buttonSound = MediaPlayer.create(this, R.raw.click_sound);

        // Zakázat výchozí zvuk tlačítek pro všechna tlačítka
        val root = findViewById<View>(R.id.linearLayout);
        disableButtonSounds(root);
    }

    override fun onDestroy() {
        super.onDestroy();
        buttonSound?.release();
        buttonSound = null;
    }

    fun disableButtonSounds(view: View) {
        if (view is android.widget.Button) {
            view.isSoundEffectsEnabled = false;
        } else if (view is android.view.ViewGroup) {
            for (i in 0 until view.childCount) {
                disableButtonSounds(view.getChildAt(i));
            }
        }
    }

    fun playButtonSound() {
        buttonSound?.let {
            if (it.isPlaying) {
                it.seekTo(0);
            };
            it.start();
        };
    }

    fun onClick(view: View) {
        playButtonSound();
        val tag = view.tag.toString();
        when {
            tag == "=" -> {
                onEqualsPressed();
            }
            tag == "C" -> {
                onClearPressed();
            }
            tag == "!" -> {
                onFactorialPressed();
            }
            tag == "del" -> {
                onDeletePressed();
            }
            tag.matches(Regex("[+\\-*/]")) -> {
                onOperatorPressed(tag);
            }
            tag.matches(Regex("\\d")) -> {
                onNumberPressed(tag);
            }
        };
        updateDisplay();
    }

    fun onNumberPressed(num: String) {
        if (!wantSecondNum) {
            if (firstNum == "0") firstNum = "";
            firstNum += num;
        } else {
            if (secondNum == "0") secondNum = "";
            secondNum += num;
        }
    }

    fun onOperatorPressed(op: String) {
        if (firstNum.isNotEmpty() && !wantSecondNum) {
            operator = op;
            wantSecondNum = true;
        }
    }

    fun onEqualsPressed() {
        if (firstNum.isNotEmpty() && operator.isNotEmpty() && secondNum.isNotEmpty()) {
            result = calculateResult();
            resolve = true;
            // Reset pro další výpočet
            firstNum = result.toString();
            secondNum = "";
            operator = "";
            wantSecondNum = false;
        }
    }

    fun onClearPressed() {
        firstNum = "";
        operator = "";
        secondNum = "";
        result = null;
        wantSecondNum = false;
        resolve = false;
    }

    fun onDeletePressed() {
        if (!wantSecondNum) {
            if (firstNum.isNotEmpty()) firstNum = firstNum.dropLast(1);
        } else {
            if (secondNum.isNotEmpty()) secondNum = secondNum.dropLast(1);
        }
    }

    fun onFactorialPressed() {
        if (!wantSecondNum && firstNum.isNotEmpty()) {
            val num = firstNum.toInt();
            calcFactorial(num);
            resolve = true;
            // Reset pro další výpočet
            firstNum = result.toString();
            secondNum = "";
            operator = "";
            wantSecondNum = false;
        }
    }

    fun updateDisplay() {
        val answearDisplay = findViewById<TextView>(R.id.AnswearDisplay);
        val problemDisplay = findViewById<TextView>(R.id.probleDisplay);

        if (resolve && result != null) {
            answearDisplay.text = result.toString();
            problemDisplay.text = "";
            resolve = false;
        } else {
            val problemText = buildString {
                append(firstNum);
                if (operator.isNotEmpty()) append(" $operator ");
                if (secondNum.isNotEmpty()) append(secondNum);
            };
            problemDisplay.text = problemText;
            answearDisplay.text = "";
        }
    }

    fun calculateResult(): BigInteger? {
        return try {
            val num1 = BigInteger(firstNum);
            val num2 = BigInteger(secondNum);
            when (operator) {
                "+" -> num1.add(num2);
                "-" -> num1.subtract(num2);
                "*" -> num1.multiply(num2);
                "/" -> if (num2 != BigInteger.ZERO) num1.divide(num2) else null;
                else -> null;
            }
        } catch (e: Exception) {
            null;
        }
    }

    fun calcFactorial(value: Int) {
        if (value < 0) {
            result = null;
            return;
        }
        var fact = BigInteger.ONE;
        for (i in 1..value) {
            fact = fact.multiply(BigInteger.valueOf(i.toLong()));
        }
        result = fact;
    }
}
