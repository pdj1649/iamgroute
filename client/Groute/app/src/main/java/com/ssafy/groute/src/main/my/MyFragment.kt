package com.ssafy.groute.src.main.my

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuInflater
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.kakao.sdk.user.UserApiClient
import com.kakao.sdk.user.rx
import com.nhn.android.naverlogin.OAuthLogin
import com.ssafy.groute.R
import com.ssafy.groute.config.ApplicationClass
import com.ssafy.groute.config.BaseFragment
import com.ssafy.groute.databinding.FragmentMyBinding
import com.ssafy.groute.src.dto.User
import com.ssafy.groute.src.main.MainActivity
import com.ssafy.groute.src.response.UserInfoResponse
import com.ssafy.groute.src.service.UserService
import com.ssafy.groute.src.viewmodel.MainViewModel
import com.ssafy.groute.src.viewmodel.MyViewModel
import com.ssafy.groute.util.RetrofitCallback
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.runBlocking

private const val TAG = "MyFragment_groute"
class MyFragment : BaseFragment<FragmentMyBinding>(FragmentMyBinding::bind, R.layout.fragment_my) {

    private lateinit var mainActivity:MainActivity
    private lateinit var userInfoResponse: UserInfoResponse
    private lateinit var intent: Intent
    private val mainViewModel : MainViewModel by activityViewModels()

    // Naver Logout 인증 변수
    lateinit var mOAuthLoginInstance : OAuthLogin

    // firebase authentication
    var mGoogleSignInClient: GoogleSignInClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity.hideMainProfileBar(true)
        // Naver Logout init
        mOAuthLoginInstance = OAuthLogin.getInstance()
        mOAuthLoginInstance.init(requireContext(), getString(R.string.naver_client_id), getString(R.string.naver_client_secret), getString(R.string.naver_client_name))

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.google_login_key))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onResume() {
        super.onResume()
        mainActivity.hideBottomNav(false)
        runBlocking {
            mainViewModel.getUserInformation(ApplicationClass.sharedPreferencesUtil.getUser().id, true)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // login User Info DataBinding
        initUserInfo()
        initNotiClickEvent()

        val pagerAdapter = MyTravelTabPageAdapter(this)
        val tabList = arrayListOf("My Travel", "Shared Travel", "Save Travel")
        pagerAdapter.addFragment(MyTravelFragment())
        pagerAdapter.addFragment(SharedTravelFragment())
        pagerAdapter.addFragment(SaveTravelFragment())

        intent = Intent(mainActivity, ProfileEditActivity::class.java)

        binding.myVpLayout.adapter = pagerAdapter
        TabLayoutMediator(binding.mypagetablayout, binding.myVpLayout){tab, position ->
            tab.text = tabList.get(position)
        }.attach()

        initUserInfo()
        initPopup()

        binding.myEditProfileTv.setOnClickListener {
            intent.putExtra("userData", userInfoResponse)
            startActivity(intent)
        }

    }

    private fun initPopup(){
        binding.myIbtnMore.setOnClickListener {
            val popup = PopupMenu(context, binding.myIbtnMore)
            MenuInflater(context).inflate(R.menu.my_menu_user_item, popup.menu)
            popup.show()
            popup.setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.menu_logout -> {
                        mainActivity.moveFragment(10)
                        return@setOnMenuItemClickListener true
                    }
                    R.id.menu_userDelete ->{
                        showDeleteUserDialog()
                        return@setOnMenuItemClickListener true
                    }
                    else ->{
                        return@setOnMenuItemClickListener false
                    }
                }
            }
        }
    }

    // 마이페이지 사용자 정보
    private fun initUserInfo() {
        runBlocking {
            mainViewModel.getUserInformation(ApplicationClass.sharedPreferencesUtil.getUser().id, true)
        }
        mainViewModel.loginUserInfo.observe(viewLifecycleOwner, {
            userInfoResponse = it
            val user = User(it.id, it.nickname, it.img.toString())
            binding.user = user
        })
    }

    // 회원 탈퇴 dialog
    private fun showDeleteUserDialog(){
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("회원 탈퇴")
            .setMessage("정말로 탈퇴하시겠습니까?")
            .setPositiveButton("YES",DialogInterface.OnClickListener{dialogInterface, id ->
                // 탈퇴기능구현
                mainViewModel.loginUserInfo.observe(viewLifecycleOwner, {
                    val type = it.type
                    Log.d(TAG, "showDeleteUserDialog: $type")
                    if(type == "kakao") {
                    val disposables = CompositeDisposable()
                    // 연결 끊기
                    UserApiClient.rx.unlink()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            Log.i(TAG, "연결 끊기 성공. SDK에서 토큰 삭제 됨")
                            }, { error ->
                                Log.e(TAG, "연결 끊기 실패", error)
                            }).addTo(disposables)
                    } else if(type == "google") {
                        FirebaseAuth.getInstance().currentUser?.delete()!!.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                            //로그아웃처리
                            FirebaseAuth.getInstance().signOut()
                            mGoogleSignInClient?.signOut()
                            Log.i(TAG, " 구글 로그인 연결 끊기 성공")
                            } else {
                                Log.i(TAG, "구글 로그인 user 삭제 실패")
                            }
                        }
                    } else if(type == "naver") {
                        mainActivity.naverTokenDelete()
                    }
                    UserService().deleteUser(ApplicationClass.sharedPreferencesUtil.getUser().id, DeleteCallback())
                    ApplicationClass.sharedPreferencesUtil.deleteUser()
                    ApplicationClass.sharedPreferencesUtil.deleteUserCookie()
                    ApplicationClass.sharedPreferencesUtil.deleteAutoLogin()
                })
            })
            .setNeutralButton("NO", null)
            .create()

        builder.show()
    }

    // Notification icon click Event
    private fun initNotiClickEvent() {
        binding.myIBtnNotification.setOnClickListener {
            mainActivity.moveFragment(22)
        }
    }

    inner class DeleteCallback() : RetrofitCallback<Boolean>{
        override fun onError(t: Throwable) {
            Log.d(TAG, "onError: ")
        }

        override fun onSuccess(code: Int, responseData: Boolean) {
            if(responseData){
                showCustomToast("탈퇴되었습니다.")
                mainActivity.moveFragment(10)
            }
        }

        override fun onFailure(code: Int) {
            Log.d(TAG, "onFailure: ")
        }
    }
}