package com.example.qylixSystem

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.qylixSystem.model.Currency
import com.example.qylixSystem.repository.RetrofitClient
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.StringReader
import java.text.SimpleDateFormat
import java.util.*


class MainViewModel() : ViewModel() {
    var currenciesXml = MutableLiveData<MutableList<Currency>>()
    var isError = MutableLiveData<Boolean>()
    fun apiGet(date: String, callback: Callback<String>) {

        RetrofitClient.getRepositoryService().getCurrencies(date)
            .enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {

                    if (response.isSuccessful) {
                        callback.onResponse(call, response)

                    } else {
                        isError.value = true
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    isError.value = true
                }
            })
    }

    /* ПОнимаю что код вообще ни по какому стандарту не подходит, но это все что я смог найти в интернете за вечер и сделать так что бы оно работало :)
       если кратко, то тут 3 запроса в сеть с 3мя колбэками, 2 запроса отправляется обычно, 3-ий запрос отправляется, когда нету ответа от завтрашнего дня

       */
    //
    fun getCurrencies(date: String): Boolean {
        // dateStatus статус для отображения на фрагменте корректной даты. true: сегодня-завтра, false: вчера-сегодня
        var dateStatus = true
        var tomorrow: MutableList<Currency>
        val cal = Calendar.getInstance()
        val format = SimpleDateFormat("MM.dd.yyyy")
        cal.time = format.parse(date)
        cal.add(Calendar.DATE, +1)

        apiGet(date, object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {


                var today = parser(response.body()!!)



                apiGet(format.format(cal.time), object : Callback<String> {
                    override fun onResponse(call: Call<String>, response: Response<String>) {


                        val xmlString = response.body()!!
                        // знаю что костыль
                        if (xmlString.length > 60) {
                            tomorrow = parser(xmlString)
                            for (i in 0 until today.size) {
                                today[i].rate2 = tomorrow[i].rate1
                            }
                            currenciesXml.postValue(today)
                        } else {
                            //если на завтра нету курсов
                            dateStatus = false
                            tomorrow = today
                            cal.add(Calendar.DATE, -2)




                            apiGet(format.format(cal.time), object : Callback<String> {


                                override fun onResponse(
                                    call: Call<String>,
                                    response: Response<String>
                                ) {
                                    today = parser(response.body()!!)
                                    for (i in 0 until today.size) {
                                        today[i].rate2 = tomorrow[i].rate1
                                    }
                                    currenciesXml.postValue(today)
                                }


                                override fun onFailure(call: Call<String>, t: Throwable) {
                                }

                            })
                        }
                    }

                    override fun onFailure(call: Call<String>, t: Throwable) {
                    }
                })

            }

            override fun onFailure(call: Call<String>, t: Throwable) {
            }
        })
        return dateStatus
    }

    // парсинг xml,  тоже какой нашел за вечер такой и использовал
    fun parser(data: String): MutableList<Currency> {
        val currencies = mutableListOf<Currency>()
        var inEntry = false
        var textValue = ""
        var currency: Currency? = null
        val factory = XmlPullParserFactory.newInstance()
        factory.isNamespaceAware = true
        val xpp = factory.newPullParser()
        xpp.setInput(StringReader(data))

        var event = xpp.eventType

        while (event != XmlPullParser.END_DOCUMENT) {
            val tagName = xpp.name
            when (event) {
                XmlPullParser.START_TAG -> {
                    if ("Currency".equals(tagName, ignoreCase = true)) {
                        inEntry = true
                        currency = Currency("", 0, "", 0.0, 0.0)
                    }
                }
                XmlPullParser.TEXT -> {
                    textValue = xpp.text
                }
                XmlPullParser.END_TAG -> {
                    if (inEntry) {

                        if ("Currency".equals(tagName, ignoreCase = true)) {
                            currencies.add(currency!!)
                            inEntry = false;
                        } else if ("CharCode".equals(tagName, ignoreCase = true)) {

                            currency!!.charCode = textValue
                        } else if ("Scale".equals(tagName, ignoreCase = true)) {
                            currency!!.scale = textValue.toInt()
                        } else if ("Name".equals(tagName, ignoreCase = true)) {
                            currency!!.name = textValue
                        } else if ("Rate".equals(tagName, ignoreCase = true)) {
                            currency!!.rate1 = textValue.toDouble()
                        }
                    }
                }

            }

            event = xpp.next();
        }
        return currencies
    }

}