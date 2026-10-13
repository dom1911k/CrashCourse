package com.crashcourse.app.scenes

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import com.crashcourse.app.data.GermanySigns
import com.crashcourse.app.signs.SignColors
import com.crashcourse.app.signs.SignDraw
import com.crashcourse.app.signs.carSide
import com.crashcourse.app.signs.circle
import com.crashcourse.app.signs.line
import com.crashcourse.app.signs.mandatoryCircle
import com.crashcourse.app.signs.person
import com.crashcourse.app.signs.poly
import com.crashcourse.app.signs.prohibitionCircle
import com.crashcourse.app.signs.rect
import com.crashcourse.app.signs.rectOutline
import com.crashcourse.app.signs.ring
import com.crashcourse.app.signs.text
import com.crashcourse.app.signs.warningTriangle

/**
 * Scene illustrations for the German scenarios. Right-hand traffic:
 * on a vertical road through x = 50, northbound traffic uses x = 55 and
 * southbound x = 45; on a horizontal road through y = 35, eastbound uses
 * y = 40 and westbound y = 30.
 */
object GermanyScenes {

    private fun sign(id: String): SignDraw = GermanySigns.all.first { it.id == id }.sign!!

    private val speed100: SignDraw = {
        prohibitionCircle(); text("100", 50f, 51f, 32f, SignColors.Black, condensed = true)
    }

    private fun DrawScope.crossroads() {
        ground()
        house(3f, 3f, 14f, 10f); house(82f, 50f, 14f, 10f); tree(88f, 10f); tree(10f, 60f); tree(24f, 56f, 2.5f)
        roadV(50f); roadH(35f)
    }

    private fun DrawScope.crossroadsMarked() {
        crossroads()
        dashed(50f, 0f, 50f, 25f); dashed(50f, 45f, 50f, 70f)
        dashed(0f, 35f, 40f, 35f); dashed(60f, 35f, 100f, 35f)
    }

    private fun DrawScope.townStreetH() {
        ground()
        rect(SceneColors.Pavement, 0f, 18f, 100f, 25f); rect(SceneColors.Pavement, 0f, 45f, 100f, 52f)
        house(4f, 4f, 16f, 11f); house(30f, 5f, 14f, 10f); house(70f, 4f, 18f, 11f)
        house(10f, 56f, 14f, 10f); house(48f, 56f, 16f, 10f); house(80f, 57f, 14f, 9f)
        roadH(35f)
        dashed(0f, 35f, 100f, 35f)
    }

    private fun DrawScope.autobahn(lanes: Int, left: Float, laneW: Float = 11f, shoulder: Boolean = true) {
        ground()
        val right = left + lanes * laneW
        rect(SceneColors.Asphalt, left, 0f, right + if (shoulder) 7f else 0f, SCENE_H)
        rect(Color(0xFF9E9E9E), left - 2.2f, 0f, left - 1.2f, SCENE_H) // guard rail
        solid(left + 0.8f, 0f, left + 0.8f, SCENE_H)
        for (i in 1 until lanes) dashed(left + i * laneW, 0f, left + i * laneW, SCENE_H, dash = 5f, gap = 4f)
        solid(right, 0f, right, SCENE_H)
        if (shoulder) rect(Color(0xFF9E9E9E), right + 8f, 0f, right + 9f, SCENE_H)
    }

    // -----------------------------------------------------------------------

    val rightBeforeLeft: SceneDraw = { r ->
        crossroads()
        you(55f, 57f)
        car(78f, 30f, 270f, SceneColors.OtherAlt)
        if (r) {
            path(71f, 30f, 30f, 30f)
            badge(1, 78f, 22f)
            badge(2, 62f, 57f)
            tag("right before left", 26f, 52f)
        }
    }

    val rettungsgasse: SceneDraw = { r ->
        autobahn(lanes = 3, left = 28f)
        val dxL = if (r) -3.5f else 0f
        val dxR = if (r) 3.5f else 0f
        for (y in floatArrayOf(10f, 26f, 42f)) {
            car(33.5f + dxL, y, 0f, SceneColors.Other)
            car(44.5f + dxR, y + 4f, 0f, if (y == 42f) SceneColors.You else SceneColors.OtherAlt)
            car(55.5f + dxR, y + 1f, 0f, SceneColors.Other)
        }
        label("YOU", 44.5f + dxR, 46f, 2.6f, Color.White)
        if (r) {
            ambulance(39f, 60f)
            path(39f, 53f, 39f, 6f)
            tag("corridor", 39f, 3.5f, 2.8f)
        } else {
            ambulance(44f, 63f)
        }
    }

    val zipper: SceneDraw = { r ->
        ground()
        roadV(50f)
        dashed(50f, 22f, 50f, 70f)
        // right lane closed by roadworks
        hatch(50f, 0f, 60f, 20f, Color(0xFFFF6F00))
        poly(SceneColors.Asphalt, 50f, 20f, 60f, 20f, 50f, 12f)
        car(45f, 6f); car(45f, 22f, color = SceneColors.OtherAlt); car(45f, 38f); car(45f, 54f, color = SceneColors.OtherAlt)
        you(55f, 30f)
        car(55f, 46f)
        car(55f, 62f, color = SceneColors.OtherAlt)
        if (r) {
            curvePath(55f, 25f, 55f, 17f, 46f, 13f)
            tick(66f, 22f)
            path(55f, 52f, 47f, 45f, SceneColors.Bad)
            cross(66f, 48f, 2.6f)
            tag("merge at the end", 76f, 32f, 3f)
        }
    }

    val speedRural: SceneDraw = { r ->
        ground()
        rect(Color(0xFFD9C27A), 0f, 0f, 100f, 22f) // field
        for (x in 5..95 step 12) tree(x.toFloat(), 58f, 3f)
        roadH(35f, 8f)
        dashed(0f, 35f, 100f, 35f, dash = 4f)
        you(30f, 39f, 90f)
        car(75f, 31f, 270f)
        if (r) {
            signAt(speed100, 58f, 14f, 14f)
            tag("no sign = 100", 82f, 14f, 3f)
        }
    }

    val speedTown: SceneDraw = { r ->
        townStreetH()
        signAt(sign("de_s_town"), 28f, 12f, 14f)
        you(15f, 40f, 90f)
        car(70f, 30f, 270f, SceneColors.OtherAlt)
        if (r) tag("= 50 km/h until the town exit", 60f, 21.5f, 3f)
    }

    val autobahnLimit: SceneDraw = { r ->
        autobahn(lanes = 2, left = 30f)
        signAt(sign("de_s_end_all"), 81f, 18f, 13f)
        you(46.5f, 46f)
        car(35.5f, 18f, color = SceneColors.OtherAlt)
        if (r) {
            signAt(sign("de_s_recommended"), 81f, 46f, 13f)
            tag("no limit · 130 recommended", 50f, 64f, 3f)
        }
    }

    val busHazards: SceneDraw = { r ->
        townStreetH()
        rect(SceneColors.Asphalt, 46f, 44f, 70f, 48f) // bus bay
        bus(58f, 41.5f, 90f, blink = Blink.HAZARD)
        ped(48f, 49f, small = true); ped(52f, 50f, small = true); ped(66f, 49.5f, small = true)
        you(22f, 40f, 90f)
        car(85f, 30f, 270f)
        if (r) {
            tag("walking speed", 22f, 31f, 3f)
            tag("oncoming too", 84f, 22f, 3f)
        }
    }

    val cyclistDistance: SceneDraw = { r ->
        townStreetH()
        bike(62f, 42f, 90f)
        if (r) {
            you(60f, 30.5f, 90f)
            measure(52f, 33f, 52f, 41f, "1.5 m")
            tag("2 m outside town", 82f, 13f, 3f)
        } else {
            you(36f, 40f, 90f)
        }
    }

    val greenArrow: SceneDraw = { r ->
        crossroadsMarked()
        stopLineH(50f, 60f, 46f)
        trafficLight(64f, 50f, Light.RED)
        rect(Color(0xFF212121), 61.8f, 57f, 66.2f, 61f, 0.5f)
        poly(SignColors.TrafficGreen, 62.6f, 58.5f, 64.5f, 58.5f, 64.5f, 57.6f, 65.8f, 59f, 64.5f, 60.4f, 64.5f, 59.5f, 62.6f, 59.5f)
        zebraAcrossH(35f, 10f, 66f, 6f)
        ped(66f, 24f)
        you(55f, 55f, blink = Blink.RIGHT)
        if (r) {
            tick(70f, 46f, 2.6f)
            tag("1. full stop", 82f, 46f, 2.8f)
            curvePath(55f, 47f, 55f, 41f, 80f, 40f)
            tag("2. then turn", 82f, 30f, 2.8f)
        }
    }

    val yellowLight: SceneDraw = { r ->
        ground()
        roadV(50f)
        dashed(50f, 0f, 50f, 70f)
        stopLineH(50f, 60f, 24f)
        roadH(14f, 6f)
        trafficLight(65f, 20f, Light.AMBER)
        if (r) {
            you(55f, 31f)
            tick(70f, 31f)
            tag("stop if you safely can", 30f, 52f, 3f)
        } else {
            you(55f, 50f)
            speedLines(55f, 55f, 0f, 1f)
        }
    }

    val alcohol: SceneDraw = { r ->
        infoCard(Color(0xFFFFF8E1))
        // beer glass
        rect(Color(0xFFFFB300), 20f, 22f, 34f, 52f, 1.5f)
        rect(Color.White, 19f, 17f, 35f, 24f, 3f)
        ring(Color(0xFFFFB300), 36.5f, 36f, 4f, 1.5f)
        // car
        withScale(0.4f, 50f, 15f) { carSide(SceneColors.You, 50f, 50f, 1.2f, Color(0xFFFFF8E1)) }
        label("+", 42f, 37f, 10f)
        tag(if (r) "max 0.5 ‰" else "? ‰", 74f, 58f, 5f)
        if (r) tag("novices & under 21: 0.0 ‰", 50f, 66f, 3f)
    }

    val phone: SceneDraw = { r -> phoneScene(r, "hands-free / voice only") }

    val emergencyVehicle: SceneDraw = { r ->
        ground()
        rect(SceneColors.Pavement, 60f, 0f, 66f, 70f); rect(SceneColors.Pavement, 34f, 0f, 40f, 70f)
        house(70f, 6f, 14f, 10f); house(72f, 40f, 14f, 10f); house(14f, 20f, 14f, 10f)
        roadV(50f)
        dashed(50f, 0f, 50f, 70f)
        car(45f, 12f, 180f)
        if (r) {
            you(57.2f, 34f, blink = Blink.RIGHT)
            tick(70f, 30f)
            ambulance(51f, 56f)
            path(51f, 48f, 51f, 8f)
            tag("move right & stop", 22f, 50f, 3f)
        } else {
            you(55f, 34f)
            ambulance(55f, 58f)
        }
    }

    val zebraCrossing: SceneDraw = { r ->
        townStreetH()
        zebraAcrossH(35f, 10f, 60f)
        signAt(sign("de_s_zebra"), 68f, 54f, 8f, post = false)
        if (r) {
            ped(60f, 36f)
            path(60f, 41f, 60f, 27f, SceneColors.Good)
            you(44f, 40f, 90f)
            tick(44f, 48f)
        } else {
            ped(60f, 48.5f)
            you(24f, 40f, 90f)
            speedLines(19f, 40f, -1f, 0f)
        }
    }

    val tramStop: SceneDraw = { r ->
        ground()
        rect(SceneColors.Pavement, 64f, 0f, 72f, 70f)
        house(76f, 8f, 14f, 10f); house(76f, 44f, 14f, 10f)
        roadV(47f, 17f)
        solid(43f, 0f, 43f, 70f, Color(0xFF9E9E9E), 0.5f); solid(51f, 0f, 51f, 70f, Color(0xFF9E9E9E), 0.5f)
        tram(47f, 22f)
        ped(56f, 26f); ped(60f, 22f); ped(66f, 18f)
        you(58f, if (r) 50f else 56f)
        if (r) {
            tick(68f, 50f)
            tag("wait – passengers first", 30f, 58f, 3f)
        }
    }

    val leftTurnOncoming: SceneDraw = { r ->
        crossroadsMarked()
        you(55f, 54f, blink = Blink.LEFT)
        car(45f, 16f, 180f, SceneColors.OtherAlt, Blink.RIGHT)
        if (r) {
            curvePath(55f, 47f, 53f, 38f, 34f, 30f)
            curvePath(45f, 23f, 47f, 32f, 66f, 40f, Color(0xFFE0A100))
            tag("turn in front of each other", 30f, 60f, 3f)
        }
    }

    val rightTurnCyclist: SceneDraw = { r ->
        crossroadsMarked()
        rect(Color(0xFFC75B4A), 58.5f, 45f, 61.5f, 70f)
        dashed(58.5f, 25f, 58.5f, 45f, Color.White, 0.5f, 2f, 2f)
        dashed(61.5f, 25f, 61.5f, 45f, Color.White, 0.5f, 2f, 2f)
        rect(Color(0xFFC75B4A), 58.5f, 0f, 61.5f, 25f)
        you(54.5f, 52f, blink = Blink.RIGHT)
        bike(60f, 61f)
        if (r) {
            path(60f, 56f, 60f, 10f, SceneColors.Good)
            badge(1, 66f, 60f)
            curvePath(54.5f, 46f, 54.5f, 40f, 80f, 40f)
            badge(2, 46f, 54f)
            tag("check mirror & blind spot", 24f, 62f, 3f)
        }
    }

    val winterTyres: SceneDraw = { r ->
        ground(SceneColors.Snow)
        roadH(35f, 9f)
        rect(Color(0x66FFFFFF), 10f, 27f, 40f, 31f, 2f); rect(Color(0x66FFFFFF), 55f, 38f, 90f, 42f, 2f)
        snowflakes()
        you(30f, 39f, 90f)
        if (r) {
            // tyre with alpine (mountain + snowflake) symbol
            circle(Color(0xFF212121), 78f, 16f, 11f)
            circle(Color(0xFF616161), 78f, 16f, 6.5f)
            poly(Color.White, 72f, 19f, 76f, 12f, 78f, 15f, 80f, 11.5f, 84f, 19f)
            tag("tyres with ❄ alpine symbol", 50f, 62f, 3f)
        }
    }

    val breakdownAutobahn: SceneDraw = { r ->
        autobahn(lanes = 2, left = 30f)
        car(57f, 14f, color = Color(0xFF795548), blink = Blink.HAZARD)
        ped(68f, 10f, Color(0xFFFFEB3B)) // hi-vis vest, behind the barrier
        signAt({ warningTriangle() }, 57f, 54f, 6f, post = false)
        car(35.5f, 40f)
        you(46.5f, 60f)
        if (r) {
            measure(62f, 19f, 62f, 52f, "≈ 200 m")
            tag("vest on · wait behind barrier", 30f, 6f, 2.8f)
        } else {
            tag("how far?", 70f, 54f, 3f)
        }
    }

    val overtakeRight: SceneDraw = { r ->
        autobahn(lanes = 2, left = 34f)
        car(39.5f, 24f, color = SceneColors.OtherAlt)
        you(50.5f, 52f)
        if (r) {
            path(50.5f, 45f, 50.5f, 8f, SceneColors.Bad)
            cross(60f, 24f)
            tag("never pass on the right", 26f, 62f, 3f)
        }
    }

    val parkingJunction: SceneDraw = { r ->
        crossroadsMarked()
        car(57.5f, 51f, color = SceneColors.OtherAlt)
        you(57.5f, 64f)
        if (r) {
            hatch(54.5f, 45f, 60f, 54f)
            measure(64f, 45f, 64f, 54f, "5 m")
            cross(57.5f, 51f, 2.4f)
        }
    }

    val fogLight: SceneDraw = { r ->
        ground()
        roadV(50f)
        dashed(50f, 0f, 50f, 70f)
        car(55f, 20f, color = SceneColors.OtherAlt)
        fog()
        you(55f, 52f)
        // rear lights glowing through fog
        circle(Color(0x88FF1744), 53f, 25f, 1.6f); circle(Color(0xCCFF1744), 53f, 25f, 0.8f)
        if (r) {
            measure(65f, 22f, 65f, 47f, "< 50 m")
            tag("rear fog light on · max 50 km/h", 32f, 64f, 3f)
        }
    }

    val railwayFlashing: SceneDraw = { r ->
        ground()
        roadV(50f)
        railH(24f)
        signAt(sign("de_s_andreas"), 66f, 36f, 9f)
        circle(SignColors.Red, 66f, 44f, 1.8f)
        for (a in 0 until 8) {
            val rad = Math.toRadians(a * 45.0)
            line(SignColors.Red, 66f + 2.6f * kotlin.math.cos(rad).toFloat(), 44f + 2.6f * kotlin.math.sin(rad).toFloat(),
                66f + 3.6f * kotlin.math.cos(rad).toFloat(), 44f + 3.6f * kotlin.math.sin(rad).toFloat(), 0.5f)
        }
        line(Color.White, 60f, 33f, 60f, 22f, 1.2f) // raised barrier
        stopLineH(50f, 60f, 38f)
        if (r) {
            you(55f, 44f)
            tick(40f, 44f)
            tag("red flashing = stop", 28f, 60f, 3f)
        } else {
            you(55f, 58f)
        }
    }

    val roundabout: SceneDraw = { r ->
        ground()
        roadV(50f); roadH(35f)
        circle(SceneColors.Asphalt, 50f, 35f, 22f)
        circle(Color(0xFF8BC34A), 50f, 35f, 10f)
        ring(SceneColors.Marking, 50f, 35f, 10.5f, 0.6f)
        signAt(sign("de_s_roundabout"), 66f, 62f, 8f, post = false)
        you(55f, 62f)
        car(38f, 26f, 225f, SceneColors.OtherAlt)
        if (r) {
            tag("no signal entering", 32f, 64f, 2.8f)
            curvePath(55f, 56f, 63f, 46f, 72f, 40f)
            car(80f, 40f, 90f, SceneColors.You, Blink.RIGHT)
            tag("signal right to leave", 80f, 52f, 2.8f)
        }
    }

    val childSeat: SceneDraw = { r ->
        infoCard(Color(0xFFF3E5F5))
        withScale(0.6f, 0f, 0f) { person(Color(0xFF6A1B9A), 55f, 72f, 1.2f) }
        // height ruler
        rect(Color.White, 48f, 8f, 52f, 64f, 0.6f)
        for (i in 0..8) line(Color(0xFF424242), 48f, 64f - i * 7f, 50.5f, 64f - i * 7f, 0.4f)
        line(SceneColors.Bad, 46f, 18f, 54f, 18f, 0.8f)
        label("150 cm", 60f, 18f, 3f, SceneColors.Bad)
        if (r) {
            tag("under 12 yrs & under 150 cm", 72f, 40f, 3f)
            tag("→ child seat", 72f, 48f, 3f)
        } else {
            tag("child seat until…?", 72f, 44f, 3f)
        }
    }

    val distance: SceneDraw = { r ->
        autobahn(lanes = 2, left = 34f)
        car(50.5f, 12f, color = SceneColors.OtherAlt)
        you(50.5f, 56f)
        if (r) {
            measure(62f, 17f, 62f, 51f, "50 m @ 100 km/h")
            tag("½ speedometer", 26f, 34f, 3f)
        }
    }

    val policeSignals: SceneDraw = { r ->
        crossroadsMarked()
        trafficLight(64f, 50f, Light.GREEN)
        // officer in the middle with raised arm
        withScale(1.8f, 50f, 35f) { ped(0f, 0f, Color(0xFF1B5E20)) }
        line(Color(0xFF1B5E20), 52.5f, 34.5f, 56f, 29.5f, 1.2f)
        circle(Color.White, 50f, 35f, 0.9f)
        tag("police", 50f, 28f, 2.6f)
        if (r) {
            you(55f, 52f)
            tick(66f, 60f)
            tag("officer beats the light", 24f, 54f, 3f)
        } else {
            you(55f, 58f)
        }
    }

    val parkingDisc: SceneDraw = { r ->
        infoCard(Color(0xFFE3F2FD))
        circle(SignColors.Blue, 38f, 35f, 22f)
        circle(Color.White, 38f, 35f, 17f)
        for (h in 0 until 12) {
            val a = Math.toRadians(h * 30.0 - 90)
            label("${if (h == 0) 12 else h}", 38f + 13.5f * kotlin.math.cos(a).toFloat(), 35f + 13.5f * kotlin.math.sin(a).toFloat(), 3f)
        }
        tag("arrived 10:10", 78f, 22f, 3.2f)
        if (r) {
            // hand pointing at 10:30 (between 10 and 11)
            val a = Math.toRadians(10.5 * 30.0 - 90)
            line(SceneColors.Bad, 38f, 35f, 38f + 10f * kotlin.math.cos(a).toFloat(), 35f + 10f * kotlin.math.sin(a).toFloat(), 1.2f)
            circle(SceneColors.Bad, 38f, 35f, 1.4f)
            tag("set 10:30", 78f, 34f, 4f, SceneColors.Good)
            tag("next half hour", 78f, 44f, 3f)
        }
    }

    val bikeStreet: SceneDraw = { r ->
        ground()
        house(68f, 6f, 14f, 10f); house(12f, 40f, 14f, 10f)
        roadV(50f)
        signAt(sign("de_s_bike_street"), 68f, 40f, 12f)
        bike(46.5f, 24f); bike(53.5f, 25f, color = Color(0xFF1565C0))
        you(52f, 54f)
        if (r) {
            tick(40f, 54f)
            tag("stay behind · max 30", 74f, 62f, 3f)
        }
    }

    val lowEmission: SceneDraw = { r ->
        infoCard(Color(0xFFE8F5E9))
        // Umweltzone sign
        rect(Color.White, 8f, 10f, 40f, 58f, 1.5f)
        rectOutline(Color(0xFF212121), 9f, 11f, 39f, 57f, 0.5f, 1f)
        ring(SignColors.Red, 24f, 26f, 10f, 2.2f)
        label("Umwelt", 24f, 26f, 4f)
        label("ZONE", 24f, 46f, 6f)
        // windscreen
        rect(Color(0xFF90A4AE), 50f, 18f, 92f, 52f, 6f)
        rect(Color(0xFFCFE6F5), 53f, 21f, 89f, 49f, 4f)
        if (r) {
            circle(Color(0xFF2E7D32), 82f, 42f, 5f)
            label("4", 82f, 42f, 5f, Color.White)
            tag("green sticker", 70f, 62f, 3.4f)
        } else {
            circle(Color(0x33000000), 82f, 42f, 5f)
            label("?", 82f, 42f, 5f, Color.White)
        }
    }

    // -----------------------------------------------------------------------

    /** Scenes keyed by the scenario id suffix used in [com.crashcourse.app.data.GermanyScenarios]. */
    val byId: Map<String, SceneDraw> by lazy {
        mapOf(
            "right_before_left" to rightBeforeLeft,
            "rettungsgasse" to rettungsgasse,
            "zipper" to zipper,
            "speed_rural" to speedRural,
            "speed_town" to speedTown,
            "autobahn_limit" to autobahnLimit,
            "bus_hazards" to busHazards,
            "cyclist_distance" to cyclistDistance,
            "green_arrow" to greenArrow,
            "yellow_light" to yellowLight,
            "alcohol" to alcohol,
            "phone" to phone,
            "emergency_vehicle" to emergencyVehicle,
            "zebra_crossing" to zebraCrossing,
            "tram_stop" to tramStop,
            "left_turn_oncoming" to leftTurnOncoming,
            "right_turn_cyclist" to rightTurnCyclist,
            "winter_tyres" to winterTyres,
            "breakdown_autobahn" to breakdownAutobahn,
            "overtake_right" to overtakeRight,
            "parking_junction" to parkingJunction,
            "fog_light" to fogLight,
            "railway_flashing" to railwayFlashing,
            "roundabout" to roundabout,
            "child_seat" to childSeat,
            "distance" to distance,
            "police_signals" to policeSignals,
            "parking_disc" to parkingDisc,
            "bike_street" to bikeStreet,
            "low_emission" to lowEmission,
        )
    }
}

/** Scales drawing commands around (px, py) – used to reuse 100-unit sign pictograms in scenes. */
internal fun DrawScope.withScale(k: Float, px: Float, py: Float, block: DrawScope.() -> Unit) =
    withTransform({
        translate(px, py); scale(k, k, Offset.Zero)
    }) { block() }

/** Shared by both countries: a phone in hand at the wheel. */
internal fun DrawScope.phoneScene(r: Boolean, allowed: String) {
    infoCard(Color(0xFFFFEBEE))
    ring(Color(0xFF424242), 34f, 38f, 20f, 4f)
    line(Color(0xFF424242), 14f, 38f, 54f, 38f, 3f)
    line(Color(0xFF424242), 34f, 38f, 34f, 58f, 3f)
    circle(Color(0xFF424242), 34f, 38f, 4f)
    rect(Color(0xFF212121), 62f, 14f, 80f, 48f, 2.5f)
    rect(Color(0xFF64B5F6), 63.5f, 17f, 78.5f, 44f, 1f)
    circle(Color(0xFFFFCC80), 71f, 50f, 6f)
    if (r) {
        cross(80f, 14f, 5f)
        tag(allowed, 50f, 64f, 3.2f)
    }
}
