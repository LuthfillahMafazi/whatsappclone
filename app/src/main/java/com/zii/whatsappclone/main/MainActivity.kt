package com.zii.whatsappclone.main

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.zii.whatsappclone.R
import com.zii.whatsappclone.adapter.SectionPagerAdapter
import com.zii.whatsappclone.fragment.ChatsFragment
import com.zii.whatsappclone.utils.PERMISSION_REQUEST_READ_CONTACT
import com.zii.whatsappclone.utils.REQUEST_NEW_CHATS
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_main.view.*
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {

    private val firebaseDb = FirebaseFirestore.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()
    private var mSectionPagerAdapter: SectionPagerAdapter? = null

    private val chatsFragment = ChatsFragment()

    companion object {
        const val PARAM_NAME = "name"
        const val PARAM_PHONE = "phone"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        chatsFragment.setFailureCallbackListener(this)

        setSupportActionBar(toolbar)
        mSectionPagerAdapter = SectionPagerAdapter(supportFragmentManager)

        container.adapter = mSectionPagerAdapter
        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))
//        resizeTabs()
        tabs.getTabAt(1)?.select()

        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> fab.hide()
                    1 -> fab.show()
                    2 -> fab.hide()
                }
            }

        })

        fab.setOnClickListener {
            onNewChat()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_profile -> onProfile()
            R.id.action_logout -> onLogout()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun onProfile() {
        startActivity(Intent(this, ProfileActivity::class.java))
//        finish()
    }

    private fun onLogout() {
        firebaseAuth.signOut()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun onNewChat() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // permission not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    android.Manifest.permission.READ_CONTACTS
                )
            ) {
                AlertDialog.Builder(this)
                    .setTitle("Contacts Permission")
                    .setMessage("This App Requires Access to Your Contacts to Initiation A Concersation")
                    .setPositiveButton("Yes") { dialog, which ->
                        requestContactPermission()
                    }
                    .setNegativeButton("No") { dialog, which ->

                    }
                    .show()
            } else {
                requestContactPermission()
            }
        } else {
            // permission granted
            startNewActivity()
        }
    }

    private fun requestContactPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.READ_CONTACTS),
            PERMISSION_REQUEST_READ_CONTACT
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST_READ_CONTACT -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startNewActivity()
                }
            }
        }
    }

    private fun startNewActivity() {
        val intent = Intent(this, ContactActivity::class.java)
        startActivityForResult(intent, REQUEST_NEW_CHATS)
    }

    class PlaceHolderFragment : Fragment() {
        companion object {
            private val ARG_SECTION_NUMBER = "Section number"
            fun newIntent(sectionNumber: Int): PlaceHolderFragment {
                val fragment = PlaceHolderFragment()
                val args = Bundle()
                args.putInt(ARG_SECTION_NUMBER, sectionNumber)
                fragment.arguments = args
                return fragment
            }
        }

        @SuppressLint("SetTextI18n")
        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
        ): View? {
            val rootView = inflater.inflate(R.layout.fragment_main, container, false)
            rootView.section_label.text =
                "Hello world, from section ${arguments?.getInt(ARG_SECTION_NUMBER)}"
            return rootView
        }
    }
}