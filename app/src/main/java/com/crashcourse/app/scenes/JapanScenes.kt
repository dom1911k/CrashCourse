package com.crashcourse.app.scenes

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.crashcourse.app.data.JapanSigns
import com.crashcourse.app.signs.SignColors
import com.crashcourse.app.signs.SignDraw
import com.crashcourse.app.signs.carSide
import com.crashcourse.app.signs.circle
import com.crashcourse.app.signs.line
import com.crashcourse.app.signs.poly
import com.crashcourse.app.signs.prohibitionCircle
import com.crashcourse.app.signs.rect
import com.crashcourse.app.signs.ring
import com.crashcourse.app.signs.text
import com.crashcourse.app.signs.warningTriangle

/**
 * Scene illustrations for the Japanese scenarios. Left-hand traffic:
 * on a vertical road through x = 50, northbound traffic uses x = 45 and
 * southbound x = 55; on a horizontal road through y = 35, eastbound uses
 * y = 30 and westbound y = 40.
 */
object JapanScenes {

    private fun sign(id: String): SignDraw = JapanSigns.all.first { it.id == id }.sign!!

    private fun jpSpeed(n: String): SignDraw = {
        prohibitionCircle(); text(n, 50f, 52f, 40f, SignColors.Blue, condensed = true)
    }

    private val night = Color(0xB30A1630)

    private fun DrawScope.crossroads() {
        ground()
        house(3f, 3f, 14f, 10f); house(82f, 50f, 14f, 10f); house(82f, 4f, 12f, 9f); tree(10f, 60f); tree(24f, 57f, 2.5f)
        roadV(50f); roadH(35f)
    }

    private fun DrawScope.crossroadsMarked() {
        crossroads()
        solid(50f, 0f, 50f, 25f); solid(50f, 45f, 50f, 70f)
        solid(0f, 35f, 40f, 35f); solid(60f, 35f, 100f, 35f)
    }

    private fun DrawScope.streetV(centre: Color = SceneColors.Marking, dashedCentre: Boolean = true) {
        ground()
        house(70f, 6f, 14f, 10f); house(72f, 42f, 14f, 10f); house(12f, 22f, 14f, 10f); tree(20f, 56f)
        roadV(50f)
        if (dashedCentre) dashed(50f, 0f, 50f, 70f, centre) else solid(50f, 0f, 50f, 70f, centre, 1f)
    }

    private fun DrawScope.expressway(left: Float = 30f) {
        ground()
        // shoulder on the left (Japan), two lanes, median on the right
        rect(SceneColors.Asphalt, left, 0f, left + 30f, SCENE_H)
        rect(Color(0xFF9E9E9E), left - 2f, 0f, left - 1f, SCENE_H)
        solid(left + 7f, 0f, left + 7f, SCENE_H)
        dashed(left + 18f, 0f, left + 18f, SCENE_H, dash = 5f, gap = 4f)
        solid(left + 29f, 0f, left + 29f, SCENE_H)
        rect(Color(0xFF9E9E9E), left + 30f, 0f, left + 31f, SCENE_H)
    }

    // -----------------------------------------------------------------------

    val keepLeft: SceneDraw = { r ->
        streetV()
        rect(SceneColors.Pavement, 14f, 40f, 40f, 62f)
        label("P", 20f, 44f, 4f, SignColors.Blue)
        car(22f, 52f, 0f, SceneColors.Other); car(30f, 54f, 0f, SceneColors.OtherAlt)
        you(36f, 47f, 90f, Blink.LEFT)
        car(55f, 16f, 180f)
        if (r) {
            curvePath(40f, 47f, 45f, 47f, 45f, 30f, SceneColors.Good)
            tick(36f, 30f)
            curvePath(40f, 49f, 55f, 49f, 55f, 64f, SceneColors.Bad)
            cross(64f, 60f, 2.6f)
            tag("keep LEFT", 76f, 32f, 3.2f)
        }
    }

    val railwayStop: SceneDraw = { r ->
        ground()
        roadV(50f)
        solid(50f, 34f, 50f, 70f)
        railH(24f)
        signAt(sign("jp_s_railway"), 33f, 46f, 9f)
        line(Color.White, 40f, 34f, 40f, 22f, 1.2f) // raised barrier
        stopLineH(40f, 50f, 36f)
        if (r) {
            you(45f, 42f)
            tick(60f, 42f)
            tag("stop · look · listen", 74f, 56f, 3f)
        } else {
            you(45f, 58f)
            tag("barriers up, no lights", 74f, 56f, 2.8f)
        }
    }

    val redTurn: SceneDraw = { r ->
        crossroadsMarked()
        stopLineH(40f, 50f, 46f)
        trafficLight(30f, 50f, Light.RED, horizontal = true, k = 0.8f)
        you(45f, 52f, blink = Blink.LEFT)
        if (r) {
            curvePath(45f, 46f, 45f, 40f, 26f, 40f, SceneColors.Bad)
            cross(30f, 44f, 2.6f)
            tag("no turning on red", 70f, 60f, 3f)
        }
    }

    val rightTurn: SceneDraw = { r ->
        crossroadsMarked()
        you(46f, 39f, 20f, Blink.RIGHT)
        car(55f, 14f, 180f, SceneColors.OtherAlt)
        zebraAcrossH(35f, 10f, 66f, 6f)
        ped(66f, 43f)
        if (r) {
            path(55f, 21f, 55f, 66f, SceneColors.Good)
            badge(1, 62f, 14f)
            curvePath(48f, 33f, 52f, 30f, 80f, 30f)
            badge(2, 38f, 39f)
            tag("then watch the crosswalk", 76f, 62f, 2.8f)
        }
    }

    val crosswalkNoSignal: SceneDraw = { r ->
        ground()
        rect(SceneColors.Pavement, 0f, 18f, 100f, 25f); rect(SceneColors.Pavement, 0f, 45f, 100f, 52f)
        house(6f, 5f, 14f, 10f); house(64f, 4f, 16f, 11f); house(30f, 56f, 14f, 10f)
        roadH(35f)
        solid(0f, 35f, 52f, 35f); solid(68f, 35f, 100f, 35f)
        zebraAcrossH(35f, 10f, 60f)
        if (r) {
            ped(60f, 34f)
            path(60f, 29f, 60f, 43f, SceneColors.Good)
            you(46f, 30f, 90f)
            tick(46f, 22f)
        } else {
            ped(60f, 22f)
            you(22f, 30f, 90f)
            speedLines(17f, 30f, -1f, 0f)
        }
    }

    val speedDefault: SceneDraw = { r ->
        ground()
        rect(Color(0xFF9CCC65), 0f, 0f, 100f, 22f)
        for (x in 5..95 step 14) tree(x.toFloat(), 60f, 3f)
        roadH(35f, 9f)
        solid(0f, 35f, 100f, 35f)
        you(30f, 30f, 90f)
        car(75f, 40f, 270f)
        if (r) {
            signAt(jpSpeed("60"), 60f, 12f, 13f)
            tag("no sign + centre line = 60", 30f, 12f, 3f)
            tag("no centre line = 30 (since 9/2026)", 50f, 56f, 2.8f)
        }
    }

    val expresswaySpeed: SceneDraw = { r ->
        expressway()
        you(42.5f, 48f)
        car(53.5f, 16f, color = SceneColors.OtherAlt)
        if (r) {
            signAt(jpSpeed("100"), 78f, 18f, 13f)
            tag("max 100 · min 50", 78f, 40f, 3.2f)
        }
    }

    val alcohol: SceneDraw = { r ->
        infoCard(Color(0xFFFFF8E1))
        rect(Color(0xFFFFB300), 20f, 22f, 34f, 52f, 1.5f)
        rect(Color.White, 19f, 17f, 35f, 24f, 3f)
        ring(Color(0xFFFFB300), 36.5f, 36f, 4f, 1.5f)
        withScale(0.4f, 50f, 15f) { carSide(SceneColors.You, 50f, 50f, 1.2f, Color(0xFFFFF8E1)) }
        label("+", 42f, 37f, 10f)
        if (r) {
            cross(27f, 34f, 6f)
            tag("0.15 mg/L ≈ zero tolerance", 50f, 62f, 3.4f)
        } else {
            tag("one beer okay?", 74f, 58f, 3.4f)
        }
    }

    val phone: SceneDraw = { r -> phoneScene(r, "mount it · set the route before driving") }

    val yellowLine: SceneDraw = { r ->
        ground()
        for (y in 4..66 step 14) tree(14f, y.toFloat(), 3f)
        roadV(50f)
        solid(49f, 0f, 49f, 70f, SceneColors.YellowLine, 0.8f); solid(51f, 0f, 51f, 70f, SceneColors.YellowLine, 0.8f)
        // slow tractor
        car(45f, 22f, 0f, Color(0xFF43A047), length = 8f, width = 6f)
        tag("tractor", 33f, 22f, 2.6f)
        you(45f, 50f)
        if (r) {
            curvePath(45f, 44f, 56f, 36f, 47f, 8f, SceneColors.Bad)
            cross(62f, 30f)
            tick(36f, 50f)
            tag("yellow line = don't cross", 74f, 60f, 2.8f)
        }
    }

    val beginnerMark: SceneDraw = { r ->
        ground()
        roadV(50f, 12f)
        dashed(50f, 0f, 50f, 70f)
        car(44f, 30f, color = Color(0xFFECEFF1))
        withScale(0.09f, 39.5f, 31f) { sign("jp_s_wakaba")() }
        you(56f, 44f, blink = Blink.LEFT)
        if (r) {
            curvePath(56f, 38f, 56f, 26f, 44f, 18f, SceneColors.Bad)
            cross(62f, 22f)
            tick(66f, 48f)
            tag("no cutting in / tailgating", 26f, 62f, 2.8f)
        }
    }

    val emergency: SceneDraw = { r ->
        crossroadsMarked()
        if (r) {
            you(41.5f, 54f, blink = Blink.LEFT)
            tick(32f, 54f)
            ambulance(47f, 64f)
            path(47f, 58f, 47f, 6f)
            tag("before the junction: pull LEFT & stop", 30f, 18f, 2.6f)
        } else {
            you(45f, 52f)
            ambulance(45f, 64f)
        }
    }

    val leftTurnBikes: SceneDraw = { r ->
        crossroadsMarked()
        you(44f, 52f, blink = Blink.LEFT)
        bike(39.5f, 61f)
        if (r) {
            path(39.5f, 56f, 39.5f, 10f, SceneColors.Good)
            badge(1, 33f, 61f)
            curvePath(44f, 46f, 44f, 40f, 22f, 40f)
            badge(2, 52f, 56f)
            tag("check left mirror + blind spot", 72f, 62f, 2.8f)
        }
    }

    val unmarkedIntersection: SceneDraw = { r ->
        crossroads()
        you(45f, 57f)
        car(22f, 30f, 90f, SceneColors.OtherAlt)
        if (r) {
            path(29f, 30f, 72f, 30f)
            badge(1, 22f, 22f)
            badge(2, 38f, 57f)
            tag("vehicle from the LEFT first", 74f, 58f, 2.8f)
        }
    }

    val widerRoad: SceneDraw = { r ->
        ground()
        house(4f, 4f, 14f, 10f); house(64f, 52f, 14f, 10f); tree(86f, 60f)
        roadH(32f, 13f)
        dashed(0f, 32f, 100f, 32f)
        roadV(50f, 5f, top = 45f)
        you(47.5f, 58f, 0f)
        car(80f, 38.5f, 270f, SceneColors.OtherAlt)
        if (r) {
            path(73f, 38.5f, 30f, 38.5f)
            badge(1, 80f, 30f)
            badge(2, 40f, 58f)
            tag("wider road goes first", 76f, 60f, 2.8f)
        }
    }

    val roundabout: SceneDraw = { r ->
        ground()
        roadV(50f); roadH(35f)
        circle(SceneColors.Asphalt, 50f, 35f, 22f)
        circle(Color(0xFF8BC34A), 50f, 35f, 10f)
        ring(SceneColors.Marking, 50f, 35f, 10.5f, 0.6f)
        signAt(sign("jp_s_roundabout"), 34f, 62f, 8f, post = false)
        you(45f, 62f)
        car(62f, 26f, 135f, SceneColors.OtherAlt)
        if (r) {
            curvePath(45f, 56f, 37f, 46f, 28f, 40f)
            car(20f, 40f, 270f, SceneColors.You, Blink.LEFT)
            tag("clockwise · signal left to leave", 68f, 62f, 2.6f)
            badge(1, 68f, 22f)
        }
    }

    val parkingRules: SceneDraw = { r ->
        crossroadsMarked()
        zebraAcrossV(50f, 10f, 49f, 5f)
        car(42.5f, 59f, color = SceneColors.OtherAlt)
        if (r) {
            hatch(40f, 45f, 45f, 63f)
            measure(34f, 51.5f, 34f, 63f, "5 m")
            cross(42.5f, 59f, 2.4f)
            tag("no stopping within 5 m", 20f, 47f, 2.6f)
        }
    }

    val stopSign: SceneDraw = { r ->
        crossroads()
        rect(night, 0f, 0f, SCENE_W, SCENE_H)
        stopLineH(40f, 50f, 46f)
        signAt(sign("jp_s_stop"), 34f, 48f, 8f)
        val carY = if (r) 51f else 60f
        poly(Color(0x55FFF59D), 43f, carY - 4f, 47f, carY - 4f, 54f, carY - 22f, 36f, carY - 22f) // headlight beams
        if (r) {
            you(45f, 51f)
            tick(58f, 51f)
            tag("full stop, every time", 74f, 62f, 3f, bg = Color(0xFFFFF59D))
        } else {
            you(45f, 60f)
        }
    }

    val tramPassengers: SceneDraw = { r ->
        ground()
        rect(SceneColors.Pavement, 26f, 0f, 33f, 70f)
        house(6f, 8f, 14f, 10f); house(6f, 44f, 14f, 10f)
        roadV(53f, 20f)
        solid(51f, 0f, 51f, 70f, Color(0xFF9E9E9E), 0.5f); solid(59f, 0f, 59f, 70f, Color(0xFF9E9E9E), 0.5f)
        tram(55f, 22f)
        ped(46f, 26f); ped(42f, 22f); ped(37f, 18f)
        you(42f, if (r) 48f else 56f)
        if (r) {
            tick(32f, 48f)
            tag("stop behind until they finish", 74f, 58f, 2.8f)
        }
    }

    val seatbelts: SceneDraw = { r ->
        infoCard(Color(0xFFE8EAF6))
        // car interior from above
        rect(Color(0xFF90A4AE), 30f, 6f, 70f, 66f, 10f)
        rect(Color(0xFFCFD8DC), 33f, 12f, 67f, 62f, 7f)
        val seats = listOf(42f to 26f, 58f to 26f, 40f to 48f, 50f to 48f, 60f to 48f)
        seats.forEachIndexed { i, (x, y) ->
            rect(Color(0xFF546E7A), x - 4f, y - 4f, x + 4f, y + 4f, 1.5f)
            if (r) line(SceneColors.Good, x - 3.5f, y - 3.5f, x + 3.5f, y + 3.5f, 1.2f)
            if (i == 4 && r) {
                rect(Color(0xFFFF8F00), x - 3f, y - 3f, x + 3f, y + 3.5f, 1.5f)
            }
        }
        if (r) {
            tag("everyone belted", 84f, 26f, 3f)
            tag("under 6: child seat", 84f, 48f, 3f, bg = Color(0xFFFFE0B2))
        } else {
            tag("who?", 84f, 36f, 3.4f)
        }
    }

    val expresswayBreakdown: SceneDraw = { r ->
        expressway()
        car(33.5f, 14f, color = Color(0xFF795548), blink = Blink.HAZARD)
        signAt({ warningTriangle() }, 33.5f, 50f, 6f, post = false)
        ped(23f, 12f, Color(0xFFFFEB3B)); ped(23f, 17f, Color(0xFFFFEB3B))
        you(53.5f, 58f)
        if (r) {
            tag("hazards · triangle · behind the guardrail", 50f, 66f, 2.6f)
            tag("call #9910 / 110", 80f, 14f, 3f)
        }
    }

    val etcLane: SceneDraw = { r ->
        ground()
        rect(SceneColors.Asphalt, 10f, 0f, 90f, 70f)
        // toll booths
        for (x in floatArrayOf(10f, 30f, 50f, 70f, 90f)) rect(Color(0xFFECEFF1), x - 2f, 16f, x + 2f, 28f, 1f)
        val lanes = listOf(20f to ("ETC" to Color(0xFF7B1FA2)), 40f to ("ETC" to Color(0xFF7B1FA2)), 60f to ("一般" to Color(0xFF2E7D32)), 80f to ("一般" to Color(0xFF2E7D32)))
        lanes.forEach { (x, l) ->
            rect(l.second, x - 6f, 4f, x + 6f, 10f, 1.5f)
            label(l.first, x, 7f, 3.4f, Color.White)
        }
        you(40f, 56f)
        if (r) {
            curvePath(40f, 50f, 42f, 40f, 60f, 32f, SceneColors.Good)
            tick(66f, 38f)
            path(40f, 50f, 40f, 32f, SceneColors.Bad)
            cross(32f, 40f, 2.6f)
            tag("no ETC card → 一般 lane", 70f, 62f, 2.8f)
        }
    }

    val horn: SceneDraw = { r ->
        infoCard(Color(0xFFFFF3E0))
        // horn icon
        poly(Color(0xFF424242), 14f, 30f, 24f, 30f, 38f, 20f, 38f, 50f, 24f, 40f, 14f, 40f)
        for (i in 0..2) ring(Color(0xFF424242), 38f, 35f, 6f + i * 5f, 0.8f)
        rect(Color(0xFFFFF3E0), 20f, 6f, 38f, 64f)
        poly(Color(0xFF424242), 14f, 30f, 24f, 30f, 38f, 20f, 38f, 50f, 24f, 40f, 14f, 40f)
        if (r) {
            cross(26f, 35f, 7f)
            car(75f, 35f, 0f, SceneColors.You, Blink.HAZARD, length = 22f, width = 12f)
            tag("say thanks with hazards ✓", 72f, 62f, 3f)
        } else {
            tag("honk to say thanks?", 72f, 35f, 3.2f)
        }
    }

    val flashingRed: SceneDraw = { r ->
        crossroads()
        rect(night, 0f, 0f, SCENE_W, SCENE_H)
        stopLineH(40f, 50f, 46f)
        trafficLight(30f, 50f, Light.RED, horizontal = true, k = 0.8f)
        circle(Color(0x66FF1744), 33f, 50f, 3.2f)
        tag("flashing", 30f, 56f, 2.6f)
        if (r) {
            you(45f, 51f)
            tick(58f, 52f)
            path(45f, 44f, 45f, 10f)
            tag("stop once, then go carefully", 72f, 63f, 2.8f, bg = Color(0xFFFFF59D))
        } else {
            you(45f, 62f)
        }
    }

    val licenceGerman: SceneDraw = { r ->
        infoCard(Color(0xFFFCE4EC))
        // German card licence
        rect(Color(0xFFF8BBD0), 8f, 16f, 44f, 40f, 2f)
        rect(Color(0xFF1565C0), 10f, 18f, 17f, 24f, 1f)
        label("D", 13.5f, 21f, 3f, Color.White)
        label("FÜHRERSCHEIN", 30f, 21f, 2.6f)
        rect(Color(0xFFBDBDBD), 11f, 26f, 20f, 37f, 0.8f)
        for (y in floatArrayOf(28f, 31f, 34f)) line(Color(0xFF9E9E9E), 23f, y, 40f, y, 0.6f)
        label("+", 50f, 30f, 8f)
        // JAF translation booklet
        rect(Color.White, 56f, 10f, 86f, 50f, 1f)
        label("翻訳文", 71f, 17f, 4.5f)
        label("JAF", 71f, 24f, 3f, Color(0xFF1565C0))
        for (y in floatArrayOf(30f, 34f, 38f, 42f)) line(Color(0xFFBDBDBD), 60f, y, 82f, y, 0.6f)
        if (r) {
            tick(44f, 16f); tick(86f, 10f)
            tag("licence + JAF translation (no IDP!)", 50f, 60f, 3f)
        } else {
            label("?", 71f, 34f, 14f, Color(0x88000000))
        }
    }

    val trafficLightOrder: SceneDraw = { r ->
        infoCard(Color(0xFFECEFF1))
        rect(Color(0xFF757575), 49f, 34f, 51f, 70f)
        if (r) {
            trafficLight(50f, 24f, Light.OFF, horizontal = true, k = 3f)
            circle(SignColors.TrafficGreen, 38.6f, 24f, 4.5f)
            circle(SignColors.Amber, 50f, 24f, 4.5f)
            circle(SignColors.Red, 61.4f, 24f, 4.5f)
            label("green", 38.6f, 12f, 3f); label("yellow", 50f, 12f, 3f); label("red", 61.4f, 12f, 3f)
            tag("green is called 'ao' (blue)", 50f, 60f, 3f)
        } else {
            trafficLight(50f, 24f, Light.OFF, horizontal = true, k = 3f)
            for (x in floatArrayOf(38.6f, 50f, 61.4f)) label("?", x, 24f, 5f, Color.White)
        }
    }

    val railwayStuck: SceneDraw = { r ->
        ground()
        roadV(50f)
        solid(50f, 0f, 50f, 14f); solid(50f, 34f, 50f, 70f)
        railH(24f)
        line(Color.White, 40f, 34f, 40f, 22f, 1.2f)
        car(45f, 8f, color = SceneColors.OtherAlt)
        car(45f, -3f)
        you(45f, 46f)
        if (r) {
            path(45f, 40f, 45f, 20f, SceneColors.Bad)
            cross(56f, 24f)
            tick(34f, 46f)
            tag("wait until there's space beyond", 72f, 60f, 2.8f)
        }
    }

    val overtakingZones: SceneDraw = { r ->
        streetV(dashedCentre = true)
        zebraAcrossV(50f, 10f, 14f, 6f)
        car(45f, 34f, color = SceneColors.OtherAlt)
        you(45f, 56f)
        if (r) {
            hatch(40f, 17f, 60f, 44f)
            measure(64f, 17f, 64f, 44f, "30 m")
            curvePath(45f, 50f, 56f, 42f, 50f, 20f, SceneColors.Bad)
            cross(36f, 30f)
        }
    }

    val schoolZone: SceneDraw = { r ->
        ground()
        house(70f, 6f, 14f, 10f); house(72f, 44f, 14f, 10f); house(14f, 6f, 12f, 9f)
        roadV(50f, 8f)
        for ((i, y) in listOf(14f, 20f, 26f, 36f).withIndex()) ped(43.5f, y, if (i % 2 == 0) Color(0xFFFFC107) else Color(0xFFE53935), small = true)
        if (r) {
            you(52f, 50f)
            path(52f, 44f, 52f, 6f, SceneColors.Good)
            tag("slow · wide berth · ready to stop", 26f, 62f, 2.6f)
        } else {
            you(46f, 56f)
            speedLines(46f, 61f, 0f, 1f)
        }
    }

    val headlights: SceneDraw = { r ->
        ground()
        for (x in 5..95 step 18) tree(x.toFloat(), 8f, 3f)
        roadH(35f, 9f)
        solid(0f, 35f, 100f, 35f)
        rect(night, 0f, 0f, SCENE_W, SCENE_H)
        you(16f, 30f, 90f)
        if (r) {
            poly(Color(0x88FFF59D), 21f, 28f, 21f, 32f, 96f, 40f, 96f, 18f)
            tag("high beam by default", 60f, 52f, 3f, bg = Color(0xFFFFF59D))
            tag("dip for oncoming / cars ahead", 60f, 60f, 2.8f, bg = Color(0xFFFFF59D))
        } else {
            poly(Color(0x66FFF59D), 21f, 28f, 21f, 32f, 44f, 36f, 44f, 25f)
        }
        ped(70f, 26f, Color(0xFF424242))
    }

    // -----------------------------------------------------------------------

    val byId: Map<String, SceneDraw> by lazy {
        mapOf(
            "keep_left" to keepLeft,
            "railway_stop" to railwayStop,
            "red_turn" to redTurn,
            "right_turn" to rightTurn,
            "crosswalk_no_signal" to crosswalkNoSignal,
            "speed_default" to speedDefault,
            "expressway_speed" to expresswaySpeed,
            "alcohol" to alcohol,
            "phone" to phone,
            "yellow_line" to yellowLine,
            "beginner_mark" to beginnerMark,
            "emergency" to emergency,
            "left_turn_bikes" to leftTurnBikes,
            "unmarked_intersection" to unmarkedIntersection,
            "wider_road" to widerRoad,
            "roundabout" to roundabout,
            "parking_rules" to parkingRules,
            "stop_sign" to stopSign,
            "tram_passengers" to tramPassengers,
            "seatbelts" to seatbelts,
            "expressway_breakdown" to expresswayBreakdown,
            "etc_lane" to etcLane,
            "horn" to horn,
            "flashing_red" to flashingRed,
            "licence_german" to licenceGerman,
            "traffic_light_order" to trafficLightOrder,
            "railway_stuck" to railwayStuck,
            "overtaking_zones" to overtakingZones,
            "school_zone" to schoolZone,
            "headlights_dusk" to headlights,
        )
    }
}
