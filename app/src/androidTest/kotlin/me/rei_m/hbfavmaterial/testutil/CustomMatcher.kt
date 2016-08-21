package me.rei_m.hbfavmaterial.testutil

import android.content.res.Resources
import android.support.design.widget.TextInputLayout
import android.support.test.espresso.matcher.BoundedMatcher
import android.view.View
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`

object CustomMatcher {

    fun withErrorText(text: String): Matcher<View> {
        return withErrorText(`is`(text))
    }

    fun withErrorText(stringMatcher: Matcher<String>): Matcher<View> {
        return object : BoundedMatcher<View, TextInputLayout>(TextInputLayout::class.java) {
            override fun describeTo(description: Description) {
                description.appendText("with Error Text: ")
                stringMatcher.describeTo(description)
            }

            override fun matchesSafely(textInputLayout: TextInputLayout): Boolean {
                val error = textInputLayout.error
                return error != null && stringMatcher.matches(error.toString())
            }
        }
    }

    fun withErrorText(resourceId: Int): Matcher<View> {
        return object : BoundedMatcher<View, TextInputLayout>(TextInputLayout::class.java) {
            private var resourceName: String? = null
            private var expectedText: String? = null

            override fun describeTo(description: Description) {
                description.appendText("with string from resource id: ")
                description.appendValue(resourceId)
                if (null != resourceName) {
                    description.appendText("[")
                    description.appendText(resourceName)
                    description.appendText("]")
                }
                if (null != expectedText) {
                    description.appendText(" value: ")
                    description.appendText(expectedText)
                }
            }

            override fun matchesSafely(textInputLayout: TextInputLayout): Boolean {
                if (null == expectedText) {
                    try {
                        expectedText = textInputLayout.resources.getString(resourceId)
                        resourceName = textInputLayout.resources.getResourceEntryName(resourceId)
                    } catch (ignored: Resources.NotFoundException) {
                        // NOP
                    }

                }
                val actualText = textInputLayout.error
                if (null != expectedText && null != actualText) {
                    // FYI: actualText may not be string ... its just a char sequence convert to string.
                    return expectedText == actualText.toString()
                } else {
                    return false
                }
            }
        }
    }

    fun hasError(): Matcher<View> {
        return object : BoundedMatcher<View, TextInputLayout>(TextInputLayout::class.java) {
            override fun describeTo(description: Description) {
                description.appendText("has Error")
            }

            override fun matchesSafely(textInputLayout: TextInputLayout): Boolean {
                val error = textInputLayout.error
                return error != null
            }
        }
    }
}