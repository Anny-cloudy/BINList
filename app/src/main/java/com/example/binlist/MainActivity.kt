package com.example.binlist

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.binlist.databinding.ActivityMainBinding
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        read() //чтение истории запросов из файла
        binding.bGet.setOnClickListener {
            getRusult(binding.editTextNumber.text.toString())
            save(binding.editTextNumber.text.toString()) //сохранение истории запросов в файл
        }

        binding.tSite.setOnClickListener {
            val openURL = Intent(Intent.ACTION_VIEW)
            openURL.data = Uri.parse("https://${binding.tSite.text}")
            startActivity(openURL)
        }
        binding.tPhone.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:${binding.tPhone.text}")
            startActivity(intent)
        }
        binding.tCountry2.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            val str = binding.tCountry2.text

            str.replace("latitude: ".toRegex(), "")
            str.replace("longitude: ".toRegex(), "")
            str.substring(1, str.length - 1)
            intent.data = Uri.parse("geo: $str")
            startActivity(intent)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getRusult(num: String) {
        val url = "https://lookup.binlist.net/$num"
        val queue = Volley.newRequestQueue(this)
        val stringRequest = StringRequest(
            com.android.volley.Request.Method.GET,
            url,
            { response ->

                val obj = JSONObject(response)

                binding.tScheme.text = obj.getString("scheme")
                binding.tBrand.text = obj.getString("brand")
                binding.tLength.text = obj.getJSONObject("number").getString("length")
                binding.tLuhn.text = obj.getJSONObject("number").getString("luhn")
                binding.tType.text = obj.getString("type")
                binding.tPrepaid.text = obj.getString("prepaid")

                binding.tCountry.text =
                    obj.getJSONObject("country").getString("emoji") + obj.getJSONObject("country")
                        .getString("name")
                binding.tCountry2.text = "(latitude: ${
                    obj.getJSONObject("country").getString("latitude")
                }, longitude: ${obj.getJSONObject("country").getString("longitude")})"

                binding.tNameBank.text = "${obj.getJSONObject("bank").getString("name")}, ${
                    obj.getJSONObject("bank").getString("city")
                }"
                binding.tSite.text = obj.getJSONObject("bank").getString("url")
                binding.tPhone.text = obj.getJSONObject("bank").getString("phone")

            },
            {

            }
        )
        queue.add(stringRequest)
    }

    private fun save(str: String) {
        try {
            val fileOutputStream: FileOutputStream =
                openFileOutput("history.txt", Context.MODE_APPEND)
            val outputStream = OutputStreamWriter(fileOutputStream)
            outputStream.appendLine(str)
            outputStream.close()
            read()
        } finally {

        }

    }

    private fun read() {

val fileName = "history.txt"
        val file = File(this.filesDir.absolutePath, fileName)
        if (file.exists()) {
        val fileInputStream: FileInputStream = openFileInput(fileName)
        val inputStream = InputStreamReader(fileInputStream)
        val text = inputStream.readLines().toList()
        for (i in text) {
            binding.listView.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, text)
        }
        inputStream.close()
   }

}
}

