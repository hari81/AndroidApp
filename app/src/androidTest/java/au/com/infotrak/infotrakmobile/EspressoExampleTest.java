package au.com.infotrak.infotrakmobile;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import au.com.infotrak.infotrakmobile.WRESScreens.WRESCreateNewChainActivity;

import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class EspressoExampleTest {

    @Rule
    public ActivityTestRule<WRESCreateNewChainActivity> mActivityRule =
            new ActivityTestRule<>(WRESCreateNewChainActivity.class);


}
