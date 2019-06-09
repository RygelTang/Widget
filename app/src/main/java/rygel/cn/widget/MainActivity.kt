package rygel.cn.widget

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import rygel.cn.calendar.bean.Solar
import rygel.cn.dateselector.DateSelector

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<DateSelector>(R.id.date).setOndateSelectListener(object : DateSelector.OnDateSelectListener{
            override fun onSelect(solar: Solar?, isLunarMode: Boolean) {
//                showToast("selected : " + solar)
            }
        })
    }

    fun showToast(str : String) = Toast.makeText(this,str,Toast.LENGTH_SHORT).show()
}
