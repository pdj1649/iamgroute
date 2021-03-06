package com.ssafy.groute.src.main.home

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import androidx.core.view.contains
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.kakao.kakaonavi.options.CoordType
import com.kakao.kakaonavi.options.RpOption
import com.kakao.kakaonavi.options.VehicleType
import com.ssafy.groute.R
import com.ssafy.groute.config.BaseFragment
import com.ssafy.groute.databinding.FragmentInfoBinding
import com.ssafy.groute.src.main.MainActivity
import com.ssafy.groute.src.viewmodel.PlaceViewModel
import kotlinx.coroutines.runBlocking
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import java.lang.Exception
import com.kakao.kakaonavi.Destination
import com.kakao.kakaonavi.KakaoNaviService
import com.kakao.kakaonavi.KakaoNaviParams
import com.kakao.kakaonavi.NaviOptions
import java.lang.RuntimeException

private const val TAG = "InfoFragment_싸피"
class InfoFragment : BaseFragment<FragmentInfoBinding>(FragmentInfoBinding::bind, R.layout.fragment_info) {
    private var placeId = -1
    private lateinit var mainActivity : MainActivity
    private val placeViewModel: PlaceViewModel by activityViewModels()
    var lat:Double = 0.0
    var lng:Double = 0.0
    var mapViewContainer:RelativeLayout? = null
    private lateinit var mapView:MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity.hideMainProfileBar(true)
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
        arguments?.let {
            placeId = it.getInt("placeId", -1)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModels = placeViewModel
//        runBlocking {
//            placeViewModel.getPlace(placeId)
//        }
        createMap()
        binding.infoBtnFindRoad.setOnClickListener {
            placeViewModel.place.observe(viewLifecycleOwner, Observer {
                lat = it.lat.toDouble()
                lng = it.lng.toDouble()
                goNavi(lat, lng)
            })

        }
    }

    fun createMap(){
        mapView = MapView(requireContext())
        val marker = MapPOIItem()
        val placeInfo = placeViewModel.place.value!!
//        placeViewModel.place.observe(viewLifecycleOwner, Observer {

            binding.kakaoMapView.addView(mapView)
            val mapPoint = MapPoint.mapPointWithGeoCoord(placeInfo.lat.toDouble(), placeInfo.lng.toDouble())
            mapView.setMapCenterPoint(mapPoint, true)
            mapView.setZoomLevel(3, true)
            marker.itemName = binding.placeDetailTvBigContent.text.toString()
            marker.mapPoint = mapPoint
            marker.markerType = MapPOIItem.MarkerType.BluePin
            marker.selectedMarkerType = MapPOIItem.MarkerType.RedPin
//        })

        mapView.addPOIItem(marker)

    }

    private fun goNavi(destLat:Double, destLng:Double){
        try {
            if (KakaoNaviService.isKakaoNaviInstalled(requireContext())) {

                val kakao: com.kakao.kakaonavi.Location =
                    Destination.newBuilder("destination", destLng, destLat).build()

                val params = KakaoNaviParams.newBuilder(kakao)
                    .setNaviOptions(
                        NaviOptions.newBuilder()
                            .setCoordType(CoordType.WGS84) // WGS84로 설정해야 경위도 좌표 사용 가능.
                            .setRpOption(RpOption.NO_AUTO)
                            .setStartAngle(200) //시작 앵글 크기 설정.
                            .setVehicleType(VehicleType.FIRST).build()
                    ).build() //길 안내 차종 타입 설정

                KakaoNaviService.navigate(requireContext(), params)

            } else {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=com.locnall.KimGiSa")
                )
                startActivity(intent)
            }
        } catch (e: Exception) {
            Log.e("네비연동 에러", e.toString() + "")
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(key:String, value:Int) =
            InfoFragment().apply {
                arguments = Bundle().apply {
                    putInt(key,value)
                }
            }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: ")
    }

    override fun onResume() {
        super.onResume()
        if(binding.kakaoMapView.contains(mapView)!!){
            try{
                createMap()
            }catch (re: RuntimeException){
                Log.d(TAG, "onResume: ${re.printStackTrace()}")
            }
        }
//        mapView.onResume()
        Log.d(TAG, "onResume: ")
    }

    override fun onPause() {
        super.onPause()
        binding.kakaoMapView.removeView(mapView)
//        mapView.onPause()
        Log.d(TAG, "onPause: ")
    }

    override fun onStop() {
        super.onStop()
        mapView.onSurfaceDestroyed()
//        binding.kakaoMapView.removeView(mapView)
        Log.d(TAG, "onStop: ")
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onSurfaceDestroyed()
        Log.d(TAG, "onDestroy: ")
    }
}
