package ai.ivira.app.features.imazh.data

import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class RandomPromptGenerator @Inject constructor() {
    private val tempRandomPrompt: MutableList<String> = randomPrompts.toMutableList()
    private var lastGeneratedPrompt: String = ""

    // FIXME: Size of initial-list MUST be greater than 1
    fun generateRandomPrompt(): String {
        if (tempRandomPrompt.isEmpty()) {
            tempRandomPrompt.addAll(randomPrompts)
        }
        val r = Random(seed = System.currentTimeMillis()).nextInt(tempRandomPrompt.size)
        val randomPrompt = tempRandomPrompt[r]
        return if (randomPrompt == lastGeneratedPrompt) {
            generateRandomPrompt()
        } else {
            tempRandomPrompt.removeAt(r).also {
                lastGeneratedPrompt = it
            }
        }
    }

    companion object {
        private val randomPrompts
            get() = listOf(
                "تصویری شاد از فردی که زیر باران می‌خندد، شبیه سازی عکاسی با دوربین برای ثبت قطرات باران",
                "تصویری با شکوه از یک جنگجوی زره پوش سوار بر اسب، جزئیات پیچیده زره، نورپردازی سینمایی",
                "پادشاه سلطنتی درحال قدم زدن در حیاط مجلل همراه دستیارانش، نمایش اقتدار این شخصیت باشکوه، نور خورشید ، جزئیات زیاد چهره ها",
                "فضانوردی نشسته روی تخته سنگی که در آب خیس شده است، انعکاس درخشان آب، مناظر عجیب و غریب، صحنه های سینمایی، فیگورهای واقعی انسان، جزئیات فوق العاده، HD",
                "تصویری دقیق از یک بطری شیشه ای حاوی یک کهکشان در حال چرخش، وضوح فوق العاده بالا",
                "یک فرمانروای رومی در کولوسئوم در حال سخنرانی برای تماشاگران، نمایش عظمت و قدرت امپراتوری روم و القای یک لحظه تاریخی",
                "قلعه ی برآمده از اقیانوس متلاطم، نورپردازی شدید، سبک کارگردانی میازاکی",
                "نقاشی جزیره ای شناور با چرخ دنده های ساعت غول پیکر، پر از موجودات افسانه ای.",
                "منظره امپرسیونیستی باغ ژاپنی در پاییز، با پلی بر روی حوض",
                "صحنه نبرد دو روبات به سبک باروک و یک قصر طلایی در پس زمینه",
                "نقاشی کوبیسم از بازار شلوغ شهری با مردم و غرفه ها",
                "کشتی در حال حرکت در دریای طوفانی، با نورپردازی دراماتیک و امواج قدرتمند",
                "نقاشی گوتیک از یک قلعه باستانی در شب، ماه کامل",
                "انبار صنعتی متروک با نورپردازی چشمگیر",
                "عکاسی ماکرو از قطرات شبنم روی تار عنکبوت، با نور خورشید صبحگاهی",
                "ایستگاه قدیمی قطار در اروپا با مسافران و چمدان هایشان",
                "تصویر فانتزی از اژدهایی که روی یک قلعه نشسته است، با آسمان طوفانی و رعد و برق در پس زمینه",
                "دنیای آخرالزمانی، ویرانه ها، یک بازمانده تنها",
                "نقاشی علمی تخیلی از یک منظره بیگانه با گیاهان ماورایی، موجودات عجیب و غریب، سیارات دور",
                "سگ ناز در حال تایپ کردن با ماشین تحریر",
                "عکس از یک کرگدن با کت و شلوار که نشسته پشت میز در یک کافه ودرحال خوردن قهوه است",
                "تصویری بسیار واقعی از یک مسیر مسابقه خارج از جاده، یک پیچ تند، دود و جرقه‌هایی که از زیر چرخ‌ها به پرواز در می‌آیند، این تصویر هیجان لحظه را به تصویر می کشد و طرفداران شاد و پر سر و صدا در پس زمینه تشویق می کنند",
                "لیونل مسی، کارتونی، فانتزی، رویایی، سورئالیسم، فوق العاده زیبا",
                "یک گربه با لباس فضایی، هنر دیجیتال، سه بعدی، تصویر بسیار دقیق، زیبا",
                "برگر و سیب زمینی سرخ شده فوق العاده واقعی و خوشمزه در بشقاب، رنگ های زنده، نورپردازی سینمایی، HDR، 8k، عکاسی زیبا",
                "گاو برقی رنگارنگ نئون، فوق واقع گرایانه، جزئیات پیچیده، سه بعدی",
                "فضای داخلی یک خانه ی لوکس با مبلمان سبز رنگ و گیاهان خانگی سرسبز و نقاشی های دیواری انتزاعی، معماری مدرن، نورپردازی زیبا، فرش دستبافت ایرانی، جزئیات زیاد، نمای باز",
                "موتورسیکلت سوار نقابدار، تمام بدن، باران شدید، جاده، چراغ های نئون، فوق واقعی",
                "عکسی از بتمن که روی پشت بام نشسته و به شهری در پایین نگاه می کند، بسیار دقیق، تاریک",
                "کریستیانو رونالدو در حال پیتزا خوردن، فیگورهای واقعی انسان"
            )
    }
}