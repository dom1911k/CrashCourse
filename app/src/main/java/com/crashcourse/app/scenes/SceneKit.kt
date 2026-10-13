package com.crashcourse.app.scenes

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.withTransform
import com.crashcourse.app.signs.SignColors
import com.crashcourse.app.signs.SignDraw
import com.crashcourse.app.signs.arrow
import com.crashcourse.app.signs.circle
import com.crashcourse.app.signs.curveArrow
import com.crashcourse.app.signs.line
import com.crashcourse.app.signs.poly
import com.crashcourse.app.signs.rect
import com.crashcourse.app.signs.text

/**
 * A traffic situation seen from above, drawn into a virtual 100 x 70 canvas.
 * [reveal] is false while the question is open and true once it has been
 * answered, so a scene can then show the correct behaviour.
 */
typealias SceneDraw = DrawScope.(reveal: Boolean) -> Unit

const val SCENE_W = 100f
const val SCENE_H = 70f

@Composable
fun SceneImage(draw: SceneDraw, reveal: Boolean, modifier: Modifier = Modifier) {
    Canvas(modifier.aspectRatio(SCENE_W / SCENE_H)) {
        val k = size.width / SCENE_W
        withTransform({ scale(k, k, Offset.Zero) }) {
            clipRect(0f, 0f, SCENE_W, SCENE_H) { draw(reveal) }
        }
    }
}

object SceneColors {
    val Grass = Color(0xFFBFD8A4)
    val Asphalt = Color(0xFF5F646B)
    val Pavement = Color(0xFFD9D6CF)
    val Marking = Color(0xFFF5F5F5)
    val YellowLine = Color(0xFFF2C200)
    val You = Color(0xFF1565C0)
    val Other = Color(0xFF8E8E93)
    val OtherAlt = Color(0xFFE0A100)
    val Good = Color(0xFF2E7D32)
    val Bad = Color(0xFFC62828)
    val Path = Color(0xFF1E88E5)
    val Blinker = Color(0xFFFF9800)
    val Rail = Color(0xFF4A3B2F)
    val Fog = Color(0xCCF2F2F2)
    val Snow = Color(0xFFF4F7FB)
    val Water = Color(0xFF90CAF9)
    val House = Color(0xFFE9C8A0)
    val Roof = Color(0xFFA0522D)
}

// ---------------------------------------------------------------------------
// Ground and roads
// ---------------------------------------------------------------------------

fun DrawScope.ground(color: Color = SceneColors.Grass) = rect(color, 0f, 0f, SCENE_W, SCENE_H)

/** Vertical road centred on [cx] with total width 2 * [half]. */
fun DrawScope.roadV(cx: Float, half: Float = 10f, top: Float = 0f, bottom: Float = SCENE_H) =
    rect(SceneColors.Asphalt, cx - half, top, cx + half, bottom)

/** Horizontal road centred on [cy]. */
fun DrawScope.roadH(cy: Float, half: Float = 10f, left: Float = 0f, right: Float = SCENE_W) =
    rect(SceneColors.Asphalt, left, cy - half, right, cy + half)

fun DrawScope.dashed(
    x1: Float, y1: Float, x2: Float, y2: Float,
    color: Color = SceneColors.Marking, w: Float = 0.8f, dash: Float = 3f, gap: Float = 3f,
) = drawLine(color, Offset(x1, y1), Offset(x2, y2), w, pathEffect = PathEffect.dashPathEffect(floatArrayOf(dash, gap)))

fun DrawScope.solid(x1: Float, y1: Float, x2: Float, y2: Float, color: Color = SceneColors.Marking, w: Float = 0.8f) =
    line(color, x1, y1, x2, y2, w, round = false)

/** Zebra crossing across a vertical road (stripes run vertically, the crossing goes left-right). */
fun DrawScope.zebraAcrossV(cx: Float, half: Float, y: Float, depth: Float = 7f) {
    var x = cx - half + 1f
    while (x < cx + half - 1f) {
        rect(SceneColors.Marking, x, y - depth / 2, x + 1.6f, y + depth / 2)
        x += 3.2f
    }
}

/** Zebra crossing across a horizontal road. */
fun DrawScope.zebraAcrossH(cy: Float, half: Float, x: Float, depth: Float = 7f) {
    var y = cy - half + 1f
    while (y < cy + half - 1f) {
        rect(SceneColors.Marking, x - depth / 2, y, x + depth / 2, y + 1.6f)
        y += 3.2f
    }
}

fun DrawScope.stopLineH(x1: Float, x2: Float, y: Float) = solid(x1, y, x2, y, w = 1.4f)
fun DrawScope.stopLineV(x: Float, y1: Float, y2: Float) = solid(x, y1, x, y2, w = 1.4f)

/** Railway track crossing horizontally. */
fun DrawScope.railH(cy: Float) {
    var x = 0f
    while (x < SCENE_W) {
        rect(SceneColors.Rail, x, cy - 4f, x + 1.4f, cy + 4f); x += 3f
    }
    solid(0f, cy - 2.2f, SCENE_W, cy - 2.2f, Color(0xFF9E9E9E), 0.8f)
    solid(0f, cy + 2.2f, SCENE_W, cy + 2.2f, Color(0xFF9E9E9E), 0.8f)
}

fun DrawScope.house(x: Float, y: Float, w: Float = 10f, h: Float = 8f) {
    rect(SceneColors.House, x, y, x + w, y + h, 0.6f)
    poly(SceneColors.Roof, x - 0.8f, y + h * 0.45f, x + w / 2, y - 1f, x + w + 0.8f, y + h * 0.45f)
}

fun DrawScope.tree(x: Float, y: Float, r: Float = 3f) {
    circle(Color(0xFF6E9F4C), x, y, r)
    circle(Color(0xFF85B560), x - r * 0.3f, y - r * 0.3f, r * 0.55f)
}

// ---------------------------------------------------------------------------
// Road users (top-down). angle: 0 = pointing up, 90 = right, 180 = down, 270 = left
// ---------------------------------------------------------------------------

enum class Blink { NONE, LEFT, RIGHT, HAZARD }

private fun DrawScope.rotated(x: Float, y: Float, angle: Float, block: DrawScope.() -> Unit) =
    translate(x, y) { rotate(angle, Offset.Zero) { block() } }

fun DrawScope.car(
    x: Float, y: Float, angle: Float = 0f,
    color: Color = SceneColors.Other, blink: Blink = Blink.NONE, length: Float = 9f, width: Float = 5f,
) = rotated(x, y, angle) {
    val l = length / 2
    val w = width / 2
    rect(Color(0x33000000), -w + 0.4f, -l + 0.6f, w + 0.4f, l + 0.6f, 1.4f) // shadow
    rect(color, -w, -l, w, l, 1.4f)
    rect(Color(0xFFBFD9F2), -w + 0.7f, -l + 1.8f, w - 0.7f, -l + 3.4f, 0.5f) // windscreen
    rect(Color(0xFFBFD9F2), -w + 0.8f, l - 2.4f, w - 0.8f, l - 1.3f, 0.4f) // rear window
    val left = blink == Blink.LEFT || blink == Blink.HAZARD
    val right = blink == Blink.RIGHT || blink == Blink.HAZARD
    if (left) {
        circle(SceneColors.Blinker, -w, -l + 0.6f, 0.9f); circle(SceneColors.Blinker, -w, l - 0.6f, 0.9f)
    }
    if (right) {
        circle(SceneColors.Blinker, w, -l + 0.6f, 0.9f); circle(SceneColors.Blinker, w, l - 0.6f, 0.9f)
    }
}

/** The learner's own car, highlighted and labelled. */
fun DrawScope.you(x: Float, y: Float, angle: Float = 0f, blink: Blink = Blink.NONE) {
    car(x, y, angle, SceneColors.You, blink)
    rect(SceneColors.You, x - 3f, y - 1.5f, x + 3f, y + 1.5f, 0.8f)
    label("YOU", x, y, 2.4f, Color.White)
}

fun DrawScope.bus(x: Float, y: Float, angle: Float = 0f, color: Color = Color(0xFFF9A825), blink: Blink = Blink.NONE) =
    car(x, y, angle, color, blink, length = 18f, width = 6f)

fun DrawScope.truck(x: Float, y: Float, angle: Float = 0f, color: Color = Color(0xFF78909C)) =
    car(x, y, angle, color, length = 16f, width = 6f)

fun DrawScope.tram(x: Float, y: Float, angle: Float = 0f) = rotated(x, y, angle) {
    rect(Color(0xFFD32F2F), -3.2f, -15f, 3.2f, 15f, 1.5f)
    for (i in -2..2) rect(Color(0xFFBFD9F2), -2.4f, i * 5.5f - 1.6f, 2.4f, i * 5.5f + 1.6f, 0.4f)
}

fun DrawScope.ambulance(x: Float, y: Float, angle: Float = 0f) = rotated(x, y, angle) {
    car(0f, 0f, 0f, Color.White, length = 11f, width = 5.4f)
    rect(SceneColors.Bad, -0.5f, -1.5f, 0.5f, 3.5f)
    rect(SceneColors.Bad, -2f, 0.5f, 2f, 1.5f)
    circle(Color(0xFF2962FF), -1.6f, -4.4f, 0.9f)
    circle(Color(0xFF2962FF), 1.6f, -4.4f, 0.9f)
    // flashing light rays
    for (dx in floatArrayOf(-1f, 1f)) {
        line(Color(0xFF2962FF), dx * 2.6f, -6f, dx * 4.6f, -8f, 0.5f)
        line(Color(0xFF2962FF), dx * 2.9f, -4.6f, dx * 5.2f, -5.2f, 0.5f)
    }
}

fun DrawScope.police(x: Float, y: Float, angle: Float = 0f) = rotated(x, y, angle) {
    car(0f, 0f, 0f, Color(0xFFECEFF1), length = 9f, width = 5f)
    rect(Color(0xFF1B5E20), -2.5f, -1f, 2.5f, 1f)
}

/** Bicycle with rider, seen from above. */
fun DrawScope.bike(x: Float, y: Float, angle: Float = 0f, color: Color = Color(0xFF2E7D32)) = rotated(x, y, angle) {
    line(Color(0xFF212121), 0f, -5f, 0f, 5f, 1.1f)
    line(Color(0xFF212121), -2f, -3.4f, 2f, -3.4f, 0.8f)
    circle(color, 0f, 0.3f, 2.2f)
    circle(Color(0xFF5D4037), 0f, -0.8f, 1.25f)
}

/** Pedestrian seen from above: shoulders + head. */
fun DrawScope.ped(x: Float, y: Float, color: Color = Color(0xFF8E24AA), small: Boolean = false) {
    val k = if (small) 0.7f else 1f
    drawOval(color, Offset(x - 1.8f * k, y - 1.1f * k), Size(3.6f * k, 2.2f * k))
    circle(Color(0xFF5D4037), x, y, 0.9f * k)
}

// ---------------------------------------------------------------------------
// Infrastructure
// ---------------------------------------------------------------------------

enum class Light { RED, AMBER, GREEN, OFF }

/** Traffic light head seen from the driver's view, placed beside the road. */
fun DrawScope.trafficLight(x: Float, y: Float, state: Light, horizontal: Boolean = false, k: Float = 1f) {
    val dim = Color(0xFF424242)
    val colors = listOf(
        if (state == Light.RED) SignColors.Red else dim,
        if (state == Light.AMBER) SignColors.Amber else dim,
        if (state == Light.GREEN) SignColors.TrafficGreen else dim,
    )
    if (horizontal) {
        // Japan: green - amber - red from left to right
        rect(Color(0xFF212121), x - 6f * k, y - 2.2f * k, x + 6f * k, y + 2.2f * k, 1f)
        circle(colors[2], x - 3.8f * k, y, 1.5f * k)
        circle(colors[1], x, y, 1.5f * k)
        circle(colors[0], x + 3.8f * k, y, 1.5f * k)
    } else {
        rect(Color(0xFF212121), x - 2.2f * k, y - 6f * k, x + 2.2f * k, y + 6f * k, 1f)
        circle(colors[0], x, y - 3.8f * k, 1.5f * k)
        circle(colors[1], x, y, 1.5f * k)
        circle(colors[2], x, y + 3.8f * k, 1.5f * k)
    }
}

/** Draws a road sign (from the sign quiz) of [size] units centred at (x, y), with a small post. */
fun DrawScope.signAt(sign: SignDraw, x: Float, y: Float, size: Float = 10f, post: Boolean = true) {
    if (post) line(Color(0xFF757575), x, y, x, y + size * 0.85f, 0.7f)
    translate(x - size / 2, y - size / 2) {
        withTransform({ scale(size / 100f, size / 100f, Offset.Zero) }) { sign() }
    }
}

/** Railway barrier arm. */
fun DrawScope.barrier(x1: Float, y: Float, x2: Float) {
    line(Color.White, x1, y, x2, y, 1.4f, round = false)
    var x = x1
    var red = true
    val step = (x2 - x1) / 6f
    repeat(6) {
        if (red) line(SignColors.Red, x, y, x + step, y, 1.4f, round = false)
        x += step; red = !red
    }
    circle(Color(0xFF424242), x1, y, 1.2f)
}

// ---------------------------------------------------------------------------
// Annotations
// ---------------------------------------------------------------------------

fun DrawScope.label(t: String, x: Float, y: Float, size: Float = 3.4f, color: Color = Color(0xFF212121), bold: Boolean = true) =
    text(t, x, y, size, color, bold)

/** Text on a white rounded tag, for distances and numbers. */
fun DrawScope.tag(t: String, x: Float, y: Float, size: Float = 3.4f, color: Color = Color(0xFF212121), bg: Color = Color.White) {
    val w = t.length * size * 0.58f + 2.4f
    rect(Color(0x55000000), x - w / 2 + 0.3f, y - size * 0.8f + 0.3f, x + w / 2 + 0.3f, y + size * 0.8f + 0.3f, 1.2f)
    rect(bg, x - w / 2, y - size * 0.8f, x + w / 2, y + size * 0.8f, 1.2f)
    text(t, x, y, size, color, true)
}

fun DrawScope.tick(x: Float, y: Float, r: Float = 3f) {
    circle(Color.White, x, y, r + 0.5f)
    circle(SceneColors.Good, x, y, r)
    line(Color.White, x - r * 0.45f, y, x - r * 0.1f, y + r * 0.4f, r * 0.28f)
    line(Color.White, x - r * 0.1f, y + r * 0.4f, x + r * 0.5f, y - r * 0.4f, r * 0.28f)
}

fun DrawScope.cross(x: Float, y: Float, r: Float = 3f) {
    circle(Color.White, x, y, r + 0.5f)
    circle(SceneColors.Bad, x, y, r)
    line(Color.White, x - r * 0.4f, y - r * 0.4f, x + r * 0.4f, y + r * 0.4f, r * 0.28f)
    line(Color.White, x - r * 0.4f, y + r * 0.4f, x + r * 0.4f, y - r * 0.4f, r * 0.28f)
}

/** Numbered order badge ("who goes first"). */
fun DrawScope.badge(n: Int, x: Float, y: Float, color: Color = SceneColors.Good) {
    circle(Color.White, x, y, 3.1f)
    circle(color, x, y, 2.6f)
    text(n.toString(), x, y, 3.6f, Color.White)
}

/** Path the vehicle should take (blue) or must not take (red). */
fun DrawScope.path(x1: Float, y1: Float, x2: Float, y2: Float, color: Color = SceneColors.Path) =
    arrow(color.copy(alpha = 0.85f), x1, y1, x2, y2, 1.6f, 3.6f)

fun DrawScope.curvePath(x1: Float, y1: Float, cx: Float, cy: Float, x2: Float, y2: Float, color: Color = SceneColors.Path) =
    curveArrow(color.copy(alpha = 0.85f), x1, y1, cx, cy, x2, y2, 1.6f, 3.6f)

/** Double-headed measurement arrow with a label. */
fun DrawScope.measure(x1: Float, y1: Float, x2: Float, y2: Float, t: String, color: Color = Color(0xFF212121)) {
    arrow(color, (x1 + x2) / 2, (y1 + y2) / 2, x1, y1, 0.6f, 1.8f)
    arrow(color, (x1 + x2) / 2, (y1 + y2) / 2, x2, y2, 0.6f, 1.8f)
    tag(t, (x1 + x2) / 2, (y1 + y2) / 2, 3f)
}

/** Hatched zone (e.g. no-parking area). */
fun DrawScope.hatch(l: Float, t: Float, r: Float, b: Float, color: Color = SceneColors.Bad) {
    rect(color.copy(alpha = 0.25f), l, t, r, b)
    clipRect(l, t, r, b) {
        var x = l - (b - t)
        while (x < r) {
            line(color.copy(alpha = 0.7f), x, b, x + (b - t), t, 0.5f, round = false); x += 2f
        }
    }
    drawRect(color, Offset(l, t), Size(r - l, b - t), style = Stroke(0.5f))
}

/** Little "motion" lines behind a road user. */
fun DrawScope.speedLines(x: Float, y: Float, dx: Float, dy: Float) {
    for (i in -1..1) {
        val ox = -dy * i * 1.4f
        val oy = dx * i * 1.4f
        line(Color(0x99FFFFFF), x + ox, y + oy, x + ox + dx * 3f, y + oy + dy * 3f, 0.5f)
    }
}

fun DrawScope.fog() = rect(SceneColors.Fog, 0f, 0f, SCENE_W, SCENE_H)

fun DrawScope.snowflakes() {
    val pts = floatArrayOf(8f, 10f, 22f, 52f, 40f, 6f, 63f, 60f, 80f, 14f, 92f, 44f, 15f, 32f, 70f, 30f, 48f, 64f, 30f, 20f)
    for (i in pts.indices step 2) {
        val x = pts[i]
        val y = pts[i + 1]
        for (a in 0 until 3) {
            val r = Math.toRadians(a * 60.0)
            val dx = (1.4 * kotlin.math.cos(r)).toFloat()
            val dy = (1.4 * kotlin.math.sin(r)).toFloat()
            line(Color.White, x - dx, y - dy, x + dx, y + dy, 0.4f)
        }
    }
}

/** Big, centred "icon card" layout for rule questions that are not a road situation. */
fun DrawScope.infoCard(bg: Color = Color(0xFFE3F2FD)) {
    rect(bg, 0f, 0f, SCENE_W, SCENE_H)
}

/** Simple curved path helper for custom shapes. */
fun pathOf(vararg xy: Float, close: Boolean = false) = Path().apply {
    moveTo(xy[0], xy[1])
    var i = 2
    while (i < xy.size) {
        lineTo(xy[i], xy[i + 1]); i += 2
    }
    if (close) close()
}

fun DrawScope.stroke(p: Path, color: Color, w: Float) = drawPath(p, color, style = Stroke(w, cap = StrokeCap.Round))
