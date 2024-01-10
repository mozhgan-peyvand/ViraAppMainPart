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
                "عکس ازیک کرگدن با کت و شلوار که نشسته پشت میز در یک کافه و درحال خوردن قهوه است.",
                "خانم زیبا،(کک و مک)،لبخند بزرگ،چشمان آبی،موهای کوتاه،آرایش تیره،عکاسی با جزئیات،نور مالیم،پرتره سر و شانه،کاور",
                "تصویری بسیار واقعی از یک مسیر مسابقه خارج از جاده،کامل با کپی های دقیق از نمادین ترین اسم سنگین جهان،که در لحظه یک پیچ تند،با دود و جرقه هایی که از زیر چرخ ها به پرواز در می آیند و اسم در اطراف پیچ میچرخد ،ثبت شده است.این تصویر هیجان لحظه را به تصویر میکشد و طرفداران شاد و پرسروصدا در پس زمینه تشویق می کنند و دست تکان میدهند.(تصویر در غروب،با چراغ های جلو به تصویر کشیده شده است",
                "گل رز صورتی ملایم،گل صد تومانی سفید چینی،گل های شکوفه سیب ریز،برگ های اکالیپتوس،شاخه های زغال اخته،شاخه های توت فلفل مسی،همه چیدمان گل های زیبا و زیبا روی یک لیوان نیکل صورتی است.لیوان روی یک کتاب سفید ضخیم با طرح تصویر جلد طلایی نشسته است.تصویرآفتابی و روشن کپی تبلیغات،فضای کپی بزرگ در بالای تصویر،فضای نگاتیو،پس زمینه تار رویایی،تصاویر زیبا"
            )
    }
}