package com.dwaplatform.android.card

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.dwaplatform.android.DWAplatform
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DWAplatformTest {


    @Test
    fun test_getCardAPI() {

        val context = InstrumentationRegistry.getTargetContext()

        val conf = DWAplatform.Configuration("http://localhost", true)
        DWAplatform.initialize(conf)
        val cardAPI = DWAplatform.getCardAPI(context)
        Assert.assertNotNull(cardAPI)
        Assert.assertNotNull(cardAPI.cardHelper)
        Assert.assertNotNull(cardAPI.log)
        Assert.assertNotNull(cardAPI.restAPI)
        Assert.assertEquals("http://localhost", cardAPI.restAPI.hostName)
        Assert.assertEquals(true, cardAPI.restAPI.sandbox)
        Assert.assertNotNull(cardAPI.restAPI.jsonHelper)
        Assert.assertNotNull(cardAPI.restAPI.queue)
        Assert.assertNotNull(cardAPI.restAPI.requestProvider)
    }
}
