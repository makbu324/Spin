
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

class description_webscrape: AsyncTask<Void, Void, Void>() {
    override fun onPreExecute() {
        super.onPreExecute()
    }

    override fun doInBackground(vararg params: Void?): Void? {

        var document: org.jsoup.nodes.Document = Jsoup.connect(URL_link).get()

        if ("/release/" in URL_link) {
            var elements = document.select("*")

            HTML_hi = elements.toString()
            Thread.sleep(1000)

            if ("archive.org/download/" in HTML_hi) {
                var g = HTML_hi.indexOf("archive.org/download/")
                image_source = HTML_hi.slice(g..g+250)
                image_source = "https://" + image_source.slice(0..<image_source.indexOf('"'))
                Log.d("image_source_pls", image_source)
            } else
                Log.d("image_source_pls", "apparently nope")

            //other info: artist, album, duration, genre
            if ("tagger-icon" in HTML_hi) {
                var g = HTML_hi.indexOf("tagger-icon")
                album_sys = HTML_hi.slice(g+150..g+1000)
                Log.d("donnie789", album_sys)
                Thread.sleep(500)
                album_sys = album_sys.slice(album_sys.indexOf('"')+7..<album_sys.length)
                album_sys = album_sys.slice(0..<album_sys.indexOf('<'))

                HTML_hi= HTML_hi.slice(g+150..g+1000)
                var h = HTML_hi.indexOf("title=") + 7
                artist_sys = HTML_hi.slice(h..h+50)
                artist_sys = artist_sys.slice(0..<artist_sys.indexOf('"'))
            }
        }
        else if ("type=artist" in URL_link) {
            var elements = document.select("*")
            Log.d("searching artist", URL_link)

            HTML_hi = elements.toString()

            ignore_this_artist = "No results found." in HTML_hi
        }
        else if ("type=release" in URL_link) {
            Log.d("im here 22222", "hi")
            var elements = document.select("*")
            HTML_hi = elements.toString()

            Log.d("come_again?", "hi")
            if (music_brainz_search) {
                Thread.sleep(5000)
                //now search for the record
                if ("/cover-art" in HTML_hi) {
                    var g = HTML_hi.indexOf("/cover-art")
                    music_brainz_link = HTML_hi.slice(g-70..g+9)
                    music_brainz_link = music_brainz_link.slice(music_brainz_link.indexOf("\"/")+2..<music_brainz_link.length)
                    Log.d("/cover-art", music_brainz_link)
                } else {
                    var p = HTML_hi.indexOf("release/")
                    Log.d("this is p", p.toString())
                    if (p != -1) {
                        music_brainz_link = HTML_hi.slice(p..p+100)
                        music_brainz_link = music_brainz_link.slice(0..<music_brainz_link.indexOf('"')).replace("/cover-art\"", "")
                    } else {
                        music_brainz_link = "NOPE"
                    }
                }

            } else {
                Thread.sleep(1000)
                for (artist in artists_array) {
                    if (artist == try_this_one || artist == "" || try_this_one == "")
                        "ignore" //ignore
                    else if (artist in HTML_hi) { //  || try_this_one in artist || artist in try_this_one
                        avoid_this_please = false
                        Log.d("test789", artist + "\\\\\\\\\\\\" + try_this_one)
                        Log.d("great789", "good")
                        comb.add(try_this_one + ":::::::" + artist)
                        break
                    }

                }
            }
            avoid_this_please = "No results found." in HTML_hi
        }
        else if ("youtube" in URL_link) {
            val regex = "\"videoId\":\"(\\w+)\"".toRegex()
            val videoIds = document.select("script")
                .map { it.data() }
                .filterNot { it.isEmpty() }
                .flatMap { regex.findAll(it) }
                .map { it.groupValues[1] }
                .distinct()

            youtube_video_id = videoIds[1].toString()

            //var thedescription = videoIds.toString()

            //Log.d("OUTPUT", thedescription)
        } else if ("lens" in URL_link) {
            var elements = document.select("*")

            var i = elements.toString().indexOf("AF_initDataCallback")

            HTML_hi = elements.toString()
        }

        return null
    }

    override fun onPostExecute(result: Void?) {
        super.onPostExecute(result)
    }
}

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
    }

    fun setUploadedImageUrl(url: String) {
        //for testing
        //var url = "https://f4.bcbits.com/img/a0305803156_65"
        val ulrn = URL(url)
        val con = ulrn.openConnection() as HttpURLConnection
        val `is` = con.inputStream
        val bmp = BitmapFactory.decodeStream(`is`)
        encodeImage(bmp)


        youtube_video_id = ""
        URL_link = ""
        HTML_hi = ""
        avoid_this_please = false
        ignore_this_artist = true
        artists_array = mutableListOf<String>()
        try_this_one = ""
        comb = mutableListOf<String>()
        skip_this = false

        music_brainz_search = false
        music_brainz_link = ""
        image_source = ""
        genre_sys = ""
        artist_sys = ""
        duration_sys = ""
        album_sys = ""

        //look up image
        var dw2 : description_webscrape = description_webscrape()
        URL_link = "https://lens.google.com/uploadbyurl?url=$url"
        dw2.execute()
        Thread.sleep(6000)

        var j = HTML_hi.indexOf("AF_initDataCallback({" )
        HTML_hi = HTML_hi.slice(j+50..j+10000)

        var search_this = ""

        var comp_array = mutableListOf<String>()

        for (i in HTML_hi.indices) {
            val alphabets = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz "
            if (i > HTML_hi.length-200)
                break
            else if (i < 2)
                "ignore"
            else if (HTML_hi[i-1] == '"' && HTML_hi[i] in alphabets) {
                var test = HTML_hi.slice(i..i+20)
                if (!("image" in test || "Image" in test || "Stock " in test || " stock " in test || "text" in test || "See more" in test  || "Show less" in test || "salient-text" in test || "GOLDMINE" in test ||  "ROSTI" in test || "To search" in test || "DetectedObject" in test ||  "Unknown Type" in test  || "UNIFIED_GRID" in test)
                    //detects english word
                    && ( (HTML_hi[i] in alphabets) && (HTML_hi[i+1] in alphabets) && (HTML_hi[i+2] in alphabets) ) ) {
                    var s = HTML_hi.slice(i..i+100) + "   "
                    var HTML_hi2 = s //index problem for some reason /\
                    var k = s.indexOf('"')
                    s = s.slice(0..< k)
                    var s2 = ""

                    if (HTML_hi2[k+1] == ',' && HTML_hi2[k+2] == '"' && HTML_hi2[k+3] in alphabets) {
                        HTML_hi2 = HTML_hi2.slice(k+3..HTML_hi2.indexOf("\""))
                        Log.d("HI", HTML_hi2)
                        s += " $HTML_hi2"
                        while ( s[s.length-1] == ' ') {
                            s = s.slice(0..(s.length-2))
                        }
                    }
                    val search_this = s
                    val alphabets_2 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
                    val numbers = "1234567890"

                    var notRando = true
                    if (search_this.length > 10 && !(" " in search_this)) {
                        var ran = search_this.length-4
                        for (r in 0..ran) {
                            if ((search_this[r] in alphabets && search_this[r+1] in numbers && search_this[r+2] in alphabets) || (search_this[r] in alphabets && search_this[r+1] in numbers && search_this[r+2] in numbers && search_this[r+3] in alphabets)) {
                                notRando = false
                                break
                            }
                        }
                    }

                    if (notRando && search_this.length in 2..50 && !("Bandcamp" == search_this || "Pitchfork" == search_this || "stock" in search_this || "merch" in search_this || "Merch" in search_this || "results" in search_this || "Wikipedia" == search_this || "Records" in search_this || "Genius" == search_this || "Apple" == search_this || "Shazam" == search_this || "TIDAL" == search_this || "Rolling Stone" == search_this || "MAGAZINE" in search_this || "Magazine" in search_this || "Pinterest" in search_this || "YouTube" == search_this || "poster" in search_this || "SoundCloud" == search_this || "ALBUMS" in search_this || "Music" in search_this || "SONG" in search_this || "Song" in search_this || ".co" in search_this || "Facebook" in search_this || "Twitter" in search_this || "Spotify" in search_this || "EXPLICIT" in search_this || "Explicit" in search_this || "PARENTAL" in search_this || "ADVISORY" in search_this || "Content" in search_this || "content" in search_this || "CONTENT" in search_this|| "Review" in search_this || "review" in search_this || "Etsy" == search_this || "Used" == search_this || "$" in search_this || "eBay" in search_this || "Instagram" in search_this || "Reddit" == search_this || "Discogs" == search_this || "The" == search_this || "Album of" in search_this || "USD" == search_this || "Iconic" == search_this || "albums" in search_this || "Albums" in search_this || "ALBUMS" in search_this || "Youtube" == search_this || "=" in search_this ||  "Search" in search_this ||  ".org" in search_this || "In stock" in search_this || "singer" in search_this || "rapper" in search_this  || "\\" in search_this || "/" in search_this || "-" in search_this || "_" in search_this || "|" in search_this ))
                        comp_array.add(search_this)
                    if ("Album by" in search_this || "album by" in search_this || "Song by" in search_this ) {
                        var s3 = HTML_hi.slice(i-100..i+50)
                        Log.d("You see OG", search_this)
                        Log.d("You see", s3)
                        s3 = s3.slice(s3.indexOf('"')+1..s3.length-1)
                        s3 = s3.slice(0..<s3.indexOf('"'))
                        comb.add(s3+ ":::::::" + search_this)
                        skip_this = true
                        break
                    }
                }
            }
        }

        dw2.cancel(true)

        Log.d("originally_0", comp_array.toString())

        if (skip_this && comb.size == 1) {
            search_this = comb[0]
        } else if (comp_array.size >= 10) {
            Log.d("Make it faster", "come_on")
            //same code as below


            val frequencyMap: MutableMap<String, Int> = HashMap()
            for (s in comp_array) {
                if (s != "") {
                    var count = frequencyMap[s]
                    if (count == null) count = 0
                    frequencyMap[s] = count + 1
                }
            }

            var first_string = ""
            var second_string = ""

            for (tup in frequencyMap.keys) {
                if (first_string == "" && second_string == "") {
                    first_string = tup
                } else if (first_string != "" && second_string == "") {
                    if (frequencyMap.getValue(first_string) <= frequencyMap.getValue(tup)) {
                        second_string = first_string
                        first_string = tup
                    } else
                        second_string = tup
                } else {
                    if (frequencyMap.getValue(second_string) < frequencyMap.getValue(tup) && frequencyMap.getValue(tup) < frequencyMap.getValue(first_string))
                        second_string = tup
                    else if (frequencyMap.getValue(second_string) < frequencyMap.getValue(tup) && frequencyMap.getValue(first_string) < frequencyMap.getValue(tup)) {
                        second_string = first_string
                        first_string = tup
                    }
                }
            }
            search_this = first_string + ":::::::" + second_string
        }
        else {
            Log.d("possible combos", "started")
            for (str in comp_array) {
                URL_link = "https://musicbrainz.org/search?query=" + str.replace(' ', '+') + "&type=artist"
                var dw4 : description_webscrape = description_webscrape()
                dw4.execute()
                Thread.sleep(3000)
                dw4.cancel(true)

                if (!(ignore_this_artist))
                    artists_array.add(str)
            }
            Log.d("artists we have", artists_array.toString())
            Thread.sleep(4000)

            for (j in comp_array.indices) {
                var str = comp_array[j]
                try_this_one = str
                URL_link = "https://musicbrainz.org/search?query=" + str.replace(' ', '+') + "&type=release"
                Log.d("check this", URL_link)
                Thread.sleep(1000)
                var dw8 : description_webscrape  = description_webscrape()
                dw8.execute()
                Thread.sleep(3000)
                dw8.cancel(true)

                if (avoid_this_please)
                    comp_array.set(j, "") //wanna see which got eliminated boi
            }

            if (comb.size == 0) {
                Log.d("gimme", "come_on")
                //we got no choice but to count reptitiveness

                val frequencyMap: MutableMap<String, Int> = HashMap()
                for (s in comp_array) {
                    if (s != "") {
                        var count = frequencyMap[s]
                        if (count == null) count = 0
                        frequencyMap[s] = count + 1
                    }
                }

                var first_string = ""
                var second_string = ""

                for (tup in frequencyMap.keys) {
                    if (first_string == "" && second_string == "") {
                        first_string = tup
                    } else if (first_string != "" && second_string == "") {
                        if (frequencyMap.getValue(first_string) <= frequencyMap.getValue(tup)) {
                            second_string = first_string
                            first_string = tup
                        } else
                            second_string = tup
                    } else {
                        if (frequencyMap.getValue(second_string) < frequencyMap.getValue(tup) && frequencyMap.getValue(tup) < frequencyMap.getValue(first_string))
                            second_string = tup
                        else if (frequencyMap.getValue(second_string) < frequencyMap.getValue(tup) && frequencyMap.getValue(first_string) < frequencyMap.getValue(tup)) {
                            second_string = first_string
                            first_string = tup
                        }
                    }
                }
                search_this = first_string + ":::::::" + second_string
            } else {
                for (str2 in comb) {
                    if (str2.length > search_this.length)
                        search_this = str2
                }
            }
        }

        Log.d("hashtags", comp_array.toString())

        Log.d("artists_to_look", artists_array.toString())

        Log.d("results_new", comb.toString())
        artists_array = mutableListOf<String>()
        comb = mutableListOf<String>()

        search_this = search_this.replace("Song by ", "")
        search_this = search_this.replace("album by ", "")
        search_this = search_this.replace("Album by ", "")
        search_this = search_this.replace("album", "")
        search_this = search_this.replace("Album", "")
        search_this = search_this.replace("Studio ", "")
        search_this = search_this.replace("Compilation ", "")
        if ("Live" in search_this) {
            search_this = search_this.replace("Live ", "")
            search_this = "Live album " + search_this
        }

        //search up a suitable album on MusicBrainz...    (Remember to fix AND)
        var search_that = ""
        if (search_this[0] == ':' || search_this[search_this.length-1] == ':')
            search_that = search_this.replace(":::::::", "")
        else
            search_that = search_this.replace(":::::::", " AND ")

        Log.d("search_that", search_that)

        URL_link = "https://musicbrainz.org/search?query=" + search_that.replace(' ', '+') + "&type=release"
        music_brainz_search = true
        var dw5 : description_webscrape = description_webscrape()
        dw5.execute()
        Thread.sleep(8000)
        dw5.cancel(true)

        if (music_brainz_link == "NOPE") {
            music_brainz_link = ""
            search_that = search_that.replace(" AND ", " ")
            URL_link = "https://musicbrainz.org/search?query=" + search_that.replace(' ', '+') + "&type=release"
            music_brainz_search = true
            var dw5 : description_webscrape = description_webscrape()
            dw5.execute()
            Thread.sleep(8000)
            dw5.cancel(true)
        }

        //Get info
        if (music_brainz_link != "") {
            URL_link = "https://musicbrainz.org/" + music_brainz_link
            Log.d("this_link_2", URL_link)
            music_brainz_search = true
            var dw5 : description_webscrape = description_webscrape()
            dw5.execute()
            Thread.sleep(8000)
            image_album_cover = image_source
            artist = artist_sys
            album = album_sys
            genre = genre_sys
            duration = duration_sys
            search_keyword = search_that
            list_of_keyword = comp_array

            Log.d("artist", artist)
            Log.d("album", album)
            Log.d("genre", genre)
            Log.d("duration", duration)
            Log.d("search_keyword", search_keyword)
            Log.d("list_of_keyword", comp_array.toString())
        }

        //turn it off
        music_brainz_search = false

        //prepare for search

        search_this = search_this.replace(' ', '+')
        Log.d("search_this", search_this)

        //search up a suitable video
        URL_link = "https://www.youtube.com/results?search_query=$search_this+album+official+audio"
        var dw : description_webscrape = description_webscrape()
        dw.execute()

        Thread.sleep(2000)

        youtube_id = youtube_video_id

        uploadedUrl.postValue(url)

        val engines: Set<String> =
            prefs.getStringSet("engines", setOf("google", "yandex", "bing")) as Set<String>
        val resultKeys = mapOf(
            "google" to SearchResult(
                "Google",
                "https://lens.google.com/uploadbyurl?url=$url",
                ContextCompat.getDrawable(context, R.drawable.record_spin)!!
            )
        )
        val results = mutableListOf<SearchResult>()
        for (engine in engines) {
            resultKeys[engine]?.let { results.add(it) }
        }
        searchResults.postValue(results)
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
