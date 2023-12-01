
import android.app.Application
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Build
import android.util.Base64
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import com.example.spotifyapitest.googleapi.AnnotateImageRequest
import com.example.spotifyapitest.googleapi.Feature
import com.example.spotifyapitest.googleapi.GoogleApiService
import com.example.spotifyapitest.googleapi.ImageRequest
import com.example.spotifyapitest.googleapi.ImageSource
import com.example.spotifyapitest.googleapi.JsonRequest
import com.example.spotifyapitest.googleapi.WebDetectionResponse
import com.example.spotifyapitest.spotifyapi.BaseResponse
import com.example.spotifyapitest.spotifyapi.SpotifyApiService
import com.example.spotifyapitest.spotifyapi.Token
import com.example.spotifyapitest.spotifyapi.UserResponse
import com.google.gson.Gson
import com.spin_cake_con.R
import com.spin_cake_con.ImageUploader
import com.spin_cake_con.TransfershUploader
import com.spin_cake_con.SearchResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.jsoup.Jsoup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

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
    val searchResults = MutableLiveData<List<String>>(emptyList())
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
    var SPOTIFY_ACCESS_TOKEN = ""

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
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onResult(url: String) {
                    if (url == "") {
                        notifyError()
                    } else {
                        setUploadedImageUrl(url)
                    }
                }
            })
    }

    fun getSearchResults(): LiveData<List<String>> = searchResults

    @RequiresApi(Build.VERSION_CODES.O)
    fun encodeImageAndGuess(bm: Bitmap) {
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val b = baos.toByteArray()
        url_thing = Base64.encodeToString(b, Base64.DEFAULT) //try

        // Launch a coroutine to get the album guesses
        viewModelScope.launch {
            val albumGuesses = getAlbumGuesses()
            Log.d("guesses", albumGuesses.toString())

            // Use the albumGuesses to get Spotify data within the same coroutine scope
            val resultDataList = getSpotifyData(albumGuesses, "https://api.spotify.com/")
            Log.d("resultDataList", resultDataList.toString())
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getAlbumGuesses(): MutableList<String> = withContext(Dispatchers.IO) {
        var albumGuessList = mutableListOf<String>()

        var inputtest = "https://i.ebayimg.com/images/g/B6IAAOSwvEpkC7N~/s-l1600.jpg"

        var googleJsonOutput: Response<WebDetectionResponse>
        var GOOGLE_VISION_API_KEY = "AIzaSyAfg_EWVJUOWTERklWMn5yVMR90teKsO_o"
        var googleURL = "https://vision.googleapis.com/"

        var encodedString = url_thing
        var imageSource = ImageSource(inputtest)
        val jsonRequest = JsonRequest(
            requests = listOf(
                AnnotateImageRequest(
                    features = listOf(Feature(type = "WEB_DETECTION", maxResults = 4)),
                    image = ImageRequest(content = encodedString)
                )
            )
        )
        val json = Gson().toJson(jsonRequest)

        // Define the media type for JSON
        val mediaType = "application/json".toMediaTypeOrNull()

        // Create a RequestBody using the JSON string and media type
        val requestBody = json.toRequestBody(mediaType)

        // Make the POST request
        val retrofitForGoogle: Retrofit = Retrofit.Builder().baseUrl(googleURL)
            .addConverterFactory(GsonConverterFactory.create()).build()
        val googleService: GoogleApiService = retrofitForGoogle.create(GoogleApiService::class.java)

        val googleListCall: Call<WebDetectionResponse> =
            googleService.getGuess(requestBody, GOOGLE_VISION_API_KEY)

        val response = googleListCall.execute() // Execute the call synchronously

        if (response.isSuccessful) {
            Log.d("google JSON response", response.body().toString())
            var entities = response.body()!!.responses[0].webDetection.webEntities
            for (entity in entities) {
                albumGuessList.add(entity.description)
            }
            Log.d("album guesses from google", albumGuessList.toString())
        } else {
            Log.i("Response!", response.body().toString())
        }

        // Return albumGuessList outside of the callback
        return@withContext albumGuessList
    }




    suspend fun getSpotifyData(
        albumGuesses: MutableList<String>,
        SEARCH_URL: String
    ): MutableList<String> = withContext(Dispatchers.IO) {
        Log.d("album guesses for spot", albumGuesses.toString())
        var queryString = ""
        var resultDataList = mutableListOf<String>()
        var censoredWords = listOf(
            "Album", "Cover", "Vinyl", "USA", "Import",
            "LP", "CD", "Soundtrack", "Phonograph record", "German Import", "Studio album", "Rock", "Indie Rock", "Hip hop music"
            ,"Record Producer"
        )

        // get access key - post request
        val retrofitForKey: Retrofit = Retrofit.Builder().baseUrl("https://accounts.spotify.com/")
            .addConverterFactory(GsonConverterFactory.create()).build()
        val service: SpotifyApiService = retrofitForKey.create(SpotifyApiService::class.java)

        val listCall: Call<Token> =
            service.getToken(
                "client_credentials",
                "2efa2b94335a4eebb74a9a8447d424a2", "e4bfd9cacf994057b5a0367d89252442"
            )

        listCall.enqueue(object : Callback<Token> {
            override fun onResponse(call: Call<Token>, response: Response<Token>) {
                if (response?.body() != null) {
                    SPOTIFY_ACCESS_TOKEN = response.body()!!.access_token.toString()
                }
                if (response?.body() == null) {
                }
            }

            override fun onFailure(call: Call<Token>, t: Throwable) {
                Log.e("Error", t.message.toString())
            }
        })

        var counter = 0
        for (guess in albumGuesses) {
            if (guess !in censoredWords && counter < 2) {
                queryString += ("$guess ")
                counter++
            }
        }

        delay(5000)
        var accessInfo = StringBuilder()
        accessInfo.append("Bearer ")
        accessInfo.append(SPOTIFY_ACCESS_TOKEN)

        val retrofitForAlbum: Retrofit = Retrofit.Builder().baseUrl("https://api.spotify.com/")
            .addConverterFactory(GsonConverterFactory.create()).build()
        val albumService: SpotifyApiService = retrofitForAlbum.create(SpotifyApiService::class.java)

        if (queryString.length > 40) {
            queryString = queryString.subSequence(0, 40).toString()
        }
        Log.d("spotify query", queryString)
        Log.d("spotify token", accessInfo.toString())
        val albumListCall: Call<BaseResponse<UserResponse>> =
            albumService.getAlbum(queryString, arrayOf("album"), "US", accessInfo.toString())

        val response = albumListCall.execute() // Execute the call synchronously

        if (response.isSuccessful) {
            var i = 0
            Log.d("spotify query URL", albumListCall.request().url.toString())
            Log.d("spotify result", response.body().toString())
            resultDataList.add(response.body()!!.albums!!.items[i].name)
            resultDataList.add(response.body()!!.albums!!.items[i].artists[i].name)
            resultDataList.add(
                response.body()!!.albums!!.items[i].release_date.subSequence(
                    0,
                    4
                ).toString()
            )
            resultDataList.add(response.body()!!.albums!!.items[i].external_urls.spotify)
            Log.d("album result!!", resultDataList.toString())
        } else {
            Log.i("Response!", "null response body")
        }
        searchResults.postValue(resultDataList)
        // Return resultDataList outside of the callback
        return@withContext resultDataList
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setUploadedImageUrl(url: String) {
        //for testing
        //var url = "https://f4.bcbits.com/img/a0305803156_65"
        val ulrn = URL(url)
        val con = ulrn.openConnection() as HttpURLConnection
        val `is` = con.inputStream
        val bmp = BitmapFactory.decodeStream(`is`)
        encodeImageAndGuess(bmp)
    }

    fun notifyError() {
        error.postValue(true)
    }

    fun getUploadedImageUrl(): LiveData<String> = uploadedUrl

    fun setImageFilePath(path: String) {
        imgPath.value = path
        croppedImgPath = path // while there is no editing
    }

    fun getImageFilePath(): LiveData<String> = imgPath
}
