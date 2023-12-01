
import android.app.Application
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.Base64
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import com.spin_cake_con.R
import com.spin_cake_con.ImageUploader
import com.spin_cake_con.TransfershUploader
import com.spin_cake_con.SearchResult
import org.jsoup.Jsoup
import java.io.ByteArrayOutputStream
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

var youtube_video_id: String = ""
var URL_link: String = ""
var HTML_hi = ""
var avoid_this_please = false
var ignore_this_artist = true
var artists_array = mutableListOf<String>()
var try_this_one = ""
var comb = mutableListOf<String>()
var skip_this = false

var music_brainz_search = false
var music_brainz_link = ""
var image_source = ""
var genre_sys = ""
var artist_sys = ""
var duration_sys = ""
var album_sys = ""


class MainViewModel(private val context: Application) : AndroidViewModel(context) {

    private var prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val imageUploader: ImageUploader = TransfershUploader()
    private val uploadedUrl = MutableLiveData("")
    private val imgPath = MutableLiveData("")
    private val appbarTitle = MutableLiveData("SPIN")
    private val allowGoBack = MutableLiveData(false)
    private val showSettingsIcon = MutableLiveData(true)
    private val showLinkIcon = MutableLiveData(true)
    private val searchResults = MutableLiveData<List<SearchResult>>(emptyList())
    private val error = MutableLiveData(false)
    var youtube_id = ""
    var image_album_cover = ""
    var artist = ""
    var duration = ""
    var genre = ""
    var album = ""
    var search_keyword = ""
    var list_of_keyword = mutableListOf<String>()
    var url_thing = ""

    var fragmentTag = ""
        get() = field
        set(value) {
            field = value
        }

    var croppedImgPath = ""

    fun setShowSettingsIcon(allow: Boolean) {
        showSettingsIcon.value = allow
    }

    fun getShowSettingsIcon(): LiveData<Boolean> = showSettingsIcon

    fun setShowLinkIcon(allow: Boolean) {
        showLinkIcon.value = allow
    }

    fun getShowLinkIcon(): LiveData<Boolean> = showLinkIcon

    fun getError(): LiveData<Boolean> = error

    fun shownError() {
        error.value = false
    }

    fun setAllowGoBack(allow: Boolean) {
        allowGoBack.value = allow
    }

    fun getAllowGoBack(): LiveData<Boolean> = allowGoBack

    fun setAppbarTitle(title: String) {
        if (title.isBlank())
            appbarTitle.value = "SPIN"
        else
            appbarTitle.value = title
    }

    fun getAppbarTitle(): LiveData<String> = appbarTitle

    fun uploadImage() {
        imageUploader.upload(
            File(croppedImgPath),
            object : ImageUploader.UploadCallback {
                override fun onResult(url: String) {
                    if (url == "") {
                        notifyError()
                    } else {
                        setUploadedImageUrl(url)
                    }
                }
            })
    }

    fun getSearchResults(): LiveData<List<SearchResult>> = searchResults

    private fun encodeImage(bm: Bitmap) {
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val b = baos.toByteArray()
        url_thing = Base64.encodeToString(b, Base64.DEFAULT) //try
        Log.d("encoded64", url_thing)
    }

    fun setUploadedImageUrl(url: String) {
        //for testing
        //var url = "https://f4.bcbits.com/img/a0305803156_65"
        val ulrn = URL(url)
        val con = ulrn.openConnection() as HttpURLConnection
        val `is` = con.inputStream
        val bmp = BitmapFactory.decodeStream(`is`)
        encodeImage(bmp)

    }

    fun notifyError() {
        error.postValue(true)
    }


    fun setImageFilePath(path: String) {
        imgPath.value = path
        croppedImgPath = path // while there is no editing
    }

    fun getImageFilePath(): LiveData<String> = imgPath
}
