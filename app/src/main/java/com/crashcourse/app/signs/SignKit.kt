package com.crashcourse.app.signs

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

/**
 * Every sign is drawn into a virtual 100 x 100 canvas. [SignImage] scales that
 * canvas to whatever size the composable gets, so all coordinates below are in
 * the 0..100 range.
 */
typealias SignDraw = DrawScope.() -> Unit

object SignColors {
    val Red = Color(0xFFC8102E)
    val Blue = Color(0xFF0A4FA3)
    val Black = Color(0xFF1A1A1A)
    val White = Color.White
    val PriorityYellow = Color(0xFFF7C600)
    val JpYellow = Color(0xFFFFD400)
    val Green = Color(0xFF00875A)
    val Grey = Color(0xFF8C8C8C)
    val Amber = Color(0xFFFFA000)
    val TrafficGreen = Color(0xFF13A538)
}

@Composable
fun SignImage(draw: SignDraw, modifier: Modifier = Modifier) {
    Canvas(modifier.aspectRatio(1f)) {
        val side = size.minDimension
        val k = side / 100f
        withTransform({
            translate((size.width - side) / 2f, (size.height - side) / 2f)
            scale(k, k, Offset.Zero)
        }) {
            draw()
        }
    }
}

// ---------------------------------------------------------------------------
// Primitive helpers
// ---------------------------------------------------------------------------

fun polyPath(vararg xy: Float): Path = Path().apply {
    moveTo(xy[0], xy[1])
    var i = 2
    while (i < xy.size) {
        lineTo(xy[i], xy[i + 1]); i += 2
    }
    close()
}

fun DrawScope.poly(color: Color, vararg xy: Float) = drawPath(polyPath(*xy), color)

fun DrawScope.polyOutline(color: Color, width: Float, vararg xy: Float) =
    drawPath(polyPath(*xy), color, style = Stroke(width, join = StrokeJoin.Round))

/** Fills a polygon and strokes it in the same colour so the corners come out rounded. */
fun DrawScope.roundPoly(color: Color, round: Float, vararg xy: Float) {
    val p = polyPath(*xy)
    drawPath(p, color)
    drawPath(p, color, style = Stroke(round, join = StrokeJoin.Round))
}

fun DrawScope.line(color: Color, x1: Float, y1: Float, x2: Float, y2: Float, w: Float, round: Boolean = true) =
    drawLine(color, Offset(x1, y1), Offset(x2, y2), w, if (round) StrokeCap.Round else StrokeCap.Butt)

fun DrawScope.circle(color: Color, cx: Float, cy: Float, r: Float) =
    drawCircle(color, r, Offset(cx, cy))

fun DrawScope.ring(color: Color, cx: Float, cy: Float, r: Float, w: Float) =
    drawCircle(color, r, Offset(cx, cy), style = Stroke(w))

fun DrawScope.rect(color: Color, l: Float, t: Float, r: Float, b: Float, corner: Float = 0f) =
    drawRoundRect(color, Offset(l, t), Size(r - l, b - t), CornerRadius(corner, corner))

fun DrawScope.rectOutline(color: Color, l: Float, t: Float, r: Float, b: Float, w: Float, corner: Float = 0f) =
    drawRoundRect(color, Offset(l, t), Size(r - l, b - t), CornerRadius(corner, corner), style = Stroke(w))

/** Draws text centred horizontally on [x] and vertically on [y]. */
fun DrawScope.text(
    t: String,
    x: Float,
    y: Float,
    size: Float,
    color: Color = SignColors.Black,
    bold: Boolean = true,
    condensed: Boolean = false,
) {
    val paint = Paint().apply {
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
        textSize = size
        this.color = color.toArgb()
        typeface = if (condensed) {
            Typeface.create("sans-serif-condensed", if (bold) Typeface.BOLD else Typeface.NORMAL)
        } else {
            Typeface.create(Typeface.SANS_SERIF, if (bold) Typeface.BOLD else Typeface.NORMAL)
        }
    }
    val fm = paint.fontMetrics
    val baseline = y - (fm.ascent + fm.descent) / 2f
    drawIntoCanvas { it.nativeCanvas.drawText(t, x, baseline, paint) }
}

private fun DrawScope.arrowHead(color: Color, tipX: Float, tipY: Float, dx: Float, dy: Float, head: Float) {
    val len = hypot(dx, dy)
    val ux = dx / len
    val uy = dy / len
    val bx = tipX - ux * head
    val by = tipY - uy * head
    val px = -uy * head * 0.62f
    val py = ux * head * 0.62f
    poly(color, tipX, tipY, bx + px, by + py, bx - px, by - py)
}

/** Straight arrow from (x1,y1) with its tip at (x2,y2). */
fun DrawScope.arrow(color: Color, x1: Float, y1: Float, x2: Float, y2: Float, w: Float, head: Float) {
    val dx = x2 - x1
    val dy = y2 - y1
    val len = hypot(dx, dy)
    val ex = x2 - dx / len * head * 0.9f
    val ey = y2 - dy / len * head * 0.9f
    line(color, x1, y1, ex, ey, w, round = false)
    arrowHead(color, x2, y2, dx, dy, head)
}

/** Arrow following a quadratic curve; the head points from the control point to the tip. */
fun DrawScope.curveArrow(
    color: Color, x1: Float, y1: Float, cx: Float, cy: Float, x2: Float, y2: Float, w: Float, head: Float,
) {
    val dx = x2 - cx
    val dy = y2 - cy
    val len = hypot(dx, dy)
    val ex = x2 - dx / len * head * 0.9f
    val ey = y2 - dy / len * head * 0.9f
    val p = Path().apply { moveTo(x1, y1); quadraticTo(cx, cy, ex, ey) }
    drawPath(p, color, style = Stroke(w, cap = StrokeCap.Butt, join = StrokeJoin.Round))
    arrowHead(color, x2, y2, dx, dy, head)
}

/** Arrow along a cubic curve. */
fun DrawScope.cubicArrow(
    color: Color, x1: Float, y1: Float, c1x: Float, c1y: Float, c2x: Float, c2y: Float,
    x2: Float, y2: Float, w: Float, head: Float,
) {
    val dx = x2 - c2x
    val dy = y2 - c2y
    val len = hypot(dx, dy)
    val ex = x2 - dx / len * head * 0.9f
    val ey = y2 - dy / len * head * 0.9f
    val p = Path().apply { moveTo(x1, y1); cubicTo(c1x, c1y, c2x, c2y, ex, ey) }
    drawPath(p, color, style = Stroke(w, cap = StrokeCap.Butt, join = StrokeJoin.Round))
    arrowHead(color, x2, y2, dx, dy, head)
}

/** Polyline arrow (e.g. a turn arrow with a right-angle bend). */
fun DrawScope.bentArrow(color: Color, w: Float, head: Float, vararg xy: Float) {
    val n = xy.size
    val tipX = xy[n - 2]
    val tipY = xy[n - 1]
    val dx = tipX - xy[n - 4]
    val dy = tipY - xy[n - 3]
    val len = hypot(dx, dy)
    val p = Path().apply {
        moveTo(xy[0], xy[1])
        var i = 2
        while (i < n - 2) {
            lineTo(xy[i], xy[i + 1]); i += 2
        }
        lineTo(tipX - dx / len * head * 0.9f, tipY - dy / len * head * 0.9f)
    }
    drawPath(p, color, style = Stroke(w, cap = StrokeCap.Butt, join = StrokeJoin.Miter))
    arrowHead(color, tipX, tipY, dx, dy, head)
}

/** Three arrows chasing each other around a circle: the roundabout symbol. */
fun DrawScope.roundaboutArrows(color: Color, clockwise: Boolean, r: Float = 18f, w: Float = 6f, head: Float = 10f) {
    val cx = 50f
    val cy = 50f
    for (i in 0 until 3) {
        val start = 90f + i * 120f
        val sweep = if (clockwise) 75f else -75f
        drawArc(
            color, start, sweep, false,
            topLeft = Offset(cx - r, cy - r), size = Size(2 * r, 2 * r), style = Stroke(w),
        )
        val endDeg = start + sweep + if (clockwise) 12f else -12f
        val a = Math.toRadians(endDeg.toDouble())
        val tx = cx + r * cos(a).toFloat()
        val ty = cy + r * sin(a).toFloat()
        val dirX = if (clockwise) -sin(a).toFloat() else sin(a).toFloat()
        val dirY = if (clockwise) cos(a).toFloat() else -cos(a).toFloat()
        arrowHead(color, tx + dirX * head * 0.35f, ty + dirY * head * 0.35f, dirX, dirY, head)
    }
}

// ---------------------------------------------------------------------------
// Sign blanks (shapes / borders)
// ---------------------------------------------------------------------------

/** German / Vienna-style warning triangle: white with red border, point up. */
fun DrawScope.warningTriangle() {
    roundPoly(SignColors.Red, 8f, 50f, 8f, 95f, 88f, 5f, 88f)
    poly(SignColors.White, 50f, 23f, 82.5f, 81f, 17.5f, 81f)
}

/** Inverted give-way triangle (red border, white centre). */
fun DrawScope.giveWayTriangle() {
    roundPoly(SignColors.Red, 8f, 5f, 12f, 95f, 12f, 50f, 92f)
    poly(SignColors.White, 18f, 19f, 82f, 19f, 50f, 76f)
}

fun DrawScope.octagonPoints(r: Float): FloatArray {
    val pts = FloatArray(16)
    for (i in 0 until 8) {
        val a = Math.toRadians((22.5 + i * 45).toDouble())
        pts[i * 2] = 50f + r * cos(a).toFloat()
        pts[i * 2 + 1] = 50f + r * sin(a).toFloat()
    }
    return pts
}

fun DrawScope.stopOctagon(label: String = "STOP") {
    poly(SignColors.White, *octagonPoints(48f))
    poly(SignColors.Red, *octagonPoints(45f))
    text(label, 50f, 51f, 24f, SignColors.White, condensed = true)
}

/** Round prohibition sign: white disc with red ring. */
fun DrawScope.prohibitionCircle(background: Color = SignColors.White) {
    circle(SignColors.Red, 50f, 50f, 46f)
    circle(background, 50f, 50f, 36f)
}

/** Diagonal red bar used on "no X" signs (top-left to bottom-right). */
fun DrawScope.prohibitionSlash() = line(SignColors.Red, 25f, 25f, 75f, 75f, 8f, round = false)

/**
 * Parallel diagonal stripes ("/") clipped to a circle of radius [r] around the
 * centre, as on the German end-of-restriction signs.
 */
fun DrawScope.endStripes(count: Int, spacing: Float, r: Float, w: Float) {
    val inv = 1f / kotlin.math.sqrt(2f)
    for (i in 0 until count) {
        val d = (i - (count - 1) / 2f) * spacing
        val half = kotlin.math.sqrt(r * r - d * d)
        // offset along (1,1), stripe runs along (1,-1)
        val cx = 50f + d * inv
        val cy = 50f + d * inv
        line(SignColors.Black, cx - half * inv, cy + half * inv, cx + half * inv, cy - half * inv, w, round = false)
    }
}

/** Round mandatory sign: blue disc with thin white rim. */
fun DrawScope.mandatoryCircle() {
    circle(SignColors.Blue, 50f, 50f, 46f)
    ring(SignColors.White, 50f, 50f, 44f, 1.5f)
}

fun DrawScope.blueSquare() {
    rect(SignColors.Blue, 6f, 6f, 94f, 94f, 6f)
    rectOutline(SignColors.White, 9f, 9f, 91f, 91f, 1.8f, 4f)
}

fun DrawScope.blueRect(top: Float, bottom: Float) {
    rect(SignColors.Blue, 4f, top, 96f, bottom, 5f)
    rectOutline(SignColors.White, 7f, top + 3f, 93f, bottom - 3f, 1.6f, 3f)
}

/** Yellow-and-white priority-road diamond (Germany). */
fun DrawScope.priorityDiamond() {
    roundPoly(SignColors.White, 4f, 50f, 4f, 96f, 50f, 50f, 96f, 4f, 50f)
    polyOutline(SignColors.Black, 1f, 50f, 3f, 97f, 50f, 50f, 97f, 3f, 50f)
    poly(SignColors.PriorityYellow, 50f, 19f, 81f, 50f, 50f, 81f, 19f, 50f)
}

/** Japanese warning sign: yellow diamond with black inner border. */
fun DrawScope.jpWarningDiamond() {
    roundPoly(SignColors.JpYellow, 4f, 50f, 3f, 97f, 50f, 50f, 97f, 3f, 50f)
    polyOutline(SignColors.Black, 3f, 50f, 9f, 91f, 50f, 50f, 91f, 9f, 50f)
}

/** Japanese 止まれ / 徐行 style inverted triangle. */
fun DrawScope.jpRedTriangle(main: String, sub: String) {
    roundPoly(SignColors.White, 6f, 4f, 10f, 96f, 10f, 50f, 93f)
    roundPoly(SignColors.Red, 3f, 11f, 14f, 89f, 14f, 50f, 84f)
    text(main, 50f, 31f, 14f, SignColors.White)
    text(sub, 50f, 48f, 8f, SignColors.White)
}

// ---------------------------------------------------------------------------
// Pictograms
// ---------------------------------------------------------------------------

/** Walking person. (cx, cy) is roughly the hip; k scales the figure. */
fun DrawScope.person(color: Color, cx: Float, cy: Float, k: Float = 1f, facingRight: Boolean = true) {
    val d = if (facingRight) 1f else -1f
    val w = 4f * k
    circle(color, cx + 1.5f * k * d, cy - 21f * k, 4.6f * k)
    line(color, cx + 1f * k * d, cy - 14f * k, cx, cy, w)
    // arms
    line(color, cx + 1f * k * d, cy - 12f * k, cx + 7f * k * d, cy - 4f * k, w * 0.8f)
    line(color, cx + 1f * k * d, cy - 12f * k, cx - 6f * k * d, cy - 5f * k, w * 0.8f)
    // legs
    line(color, cx, cy, cx + 7f * k * d, cy + 14f * k, w)
    line(color, cx, cy, cx - 6f * k * d, cy + 14f * k, w)
}

/** Car seen from the side. (cx, cy) is the centre of the body; ~36k wide. */
fun DrawScope.carSide(color: Color, cx: Float, cy: Float, k: Float = 1f, background: Color = SignColors.White) {
    rect(color, cx - 18f * k, cy - 3f * k, cx + 18f * k, cy + 6f * k, 2.5f * k)
    poly(color, cx - 10f * k, cy - 2f * k, cx - 6f * k, cy - 10f * k, cx + 7f * k, cy - 10f * k, cx + 12f * k, cy - 2f * k)
    circle(background, cx - 10f * k, cy + 7f * k, 5f * k)
    circle(background, cx + 10f * k, cy + 7f * k, 5f * k)
    circle(color, cx - 10f * k, cy + 7f * k, 3.6f * k)
    circle(color, cx + 10f * k, cy + 7f * k, 3.6f * k)
}

/** Car seen from behind (used on no-overtaking and slippery-road signs). */
fun DrawScope.carRear(color: Color, cx: Float, cy: Float, k: Float = 1f, background: Color = SignColors.White) {
    poly(color, cx - 8f * k, cy - 12f * k, cx + 8f * k, cy - 12f * k, cx + 11f * k, cy - 3f * k, cx - 11f * k, cy - 3f * k)
    poly(background, cx - 6.5f * k, cy - 10f * k, cx + 6.5f * k, cy - 10f * k, cx + 8.5f * k, cy - 4.5f * k, cx - 8.5f * k, cy - 4.5f * k)
    rect(color, cx - 13f * k, cy - 4f * k, cx + 13f * k, cy + 6f * k, 2f * k)
    rect(color, cx - 12f * k, cy + 5f * k, cx - 7f * k, cy + 10f * k, 1f * k)
    rect(color, cx + 7f * k, cy + 5f * k, cx + 12f * k, cy + 10f * k, 1f * k)
}

/** Lorry seen from the side, cab on the left. */
fun DrawScope.truckSide(color: Color, cx: Float, cy: Float, k: Float = 1f, background: Color = SignColors.White) {
    rect(color, cx - 6f * k, cy - 14f * k, cx + 22f * k, cy + 4f * k, 1f * k)
    poly(color, cx - 22f * k, cy + 4f * k, cx - 22f * k, cy - 4f * k, cx - 17f * k, cy - 10f * k, cx - 8f * k, cy - 10f * k, cx - 8f * k, cy + 4f * k)
    poly(background, cx - 19.5f * k, cy - 4f * k, cx - 16f * k, cy - 8f * k, cx - 10.5f * k, cy - 8f * k, cx - 10.5f * k, cy - 4f * k)
    for (x in floatArrayOf(-15f, 8f, 16f)) {
        circle(background, cx + x * k, cy + 6f * k, 5f * k)
        circle(color, cx + x * k, cy + 6f * k, 3.6f * k)
    }
}

fun DrawScope.bicycle(color: Color, cx: Float, cy: Float, k: Float = 1f, rider: Boolean = false) {
    val w = 2.6f * k
    val r = 8f * k
    ring(color, cx - 12f * k, cy + 6f * k, r, w)
    ring(color, cx + 12f * k, cy + 6f * k, r, w)
    // frame
    line(color, cx - 12f * k, cy + 6f * k, cx - 3f * k, cy - 6f * k, w)
    line(color, cx - 3f * k, cy - 6f * k, cx + 9f * k, cy - 6f * k, w)
    line(color, cx + 9f * k, cy - 6f * k, cx + 12f * k, cy + 6f * k, w)
    line(color, cx - 12f * k, cy + 6f * k, cx + 1f * k, cy + 6f * k, w)
    line(color, cx + 1f * k, cy + 6f * k, cx + 9f * k, cy - 6f * k, w)
    line(color, cx + 1f * k, cy + 6f * k, cx - 4f * k, cy - 9f * k, w)
    line(color, cx - 7f * k, cy - 9f * k, cx - 1f * k, cy - 9f * k, w)
    line(color, cx + 9f * k, cy - 6f * k, cx + 8f * k, cy - 11f * k, w)
    line(color, cx + 6f * k, cy - 11f * k, cx + 11f * k, cy - 11f * k, w)
    if (rider) {
        circle(color, cx + 3f * k, cy - 26f * k, 4f * k)
        line(color, cx + 1f * k, cy - 21f * k, cx - 4f * k, cy - 10f * k, 3.5f * k)
        line(color, cx + 1f * k, cy - 19f * k, cx + 8f * k, cy - 12f * k, 3f * k)
        line(color, cx - 4f * k, cy - 10f * k, cx + 1f * k, cy + 4f * k, 3f * k)
    }
}

/** Leaping deer. */
fun DrawScope.deer(color: Color, cx: Float, cy: Float, k: Float = 1f) {
    // body
    poly(
        color,
        cx - 14f * k, cy - 2f * k, cx + 8f * k, cy - 6f * k, cx + 12f * k, cy - 4f * k,
        cx + 10f * k, cy + 3f * k, cx - 12f * k, cy + 5f * k,
    )
    // neck and head
    poly(color, cx + 6f * k, cy - 5f * k, cx + 13f * k, cy - 17f * k, cx + 17f * k, cy - 15f * k, cx + 12f * k, cy - 2f * k)
    poly(color, cx + 12f * k, cy - 19f * k, cx + 21f * k, cy - 15f * k, cx + 18f * k, cy - 12f * k, cx + 12f * k, cy - 14f * k)
    // antlers
    line(color, cx + 14f * k, cy - 18f * k, cx + 10f * k, cy - 27f * k, 1.8f * k)
    line(color, cx + 12f * k, cy - 23f * k, cx + 7f * k, cy - 25f * k, 1.8f * k)
    line(color, cx + 15f * k, cy - 18f * k, cx + 17f * k, cy - 27f * k, 1.8f * k)
    // legs: front legs reaching forward, hind legs pushing back
    line(color, cx + 9f * k, cy + 1f * k, cx + 20f * k, cy + 4f * k, 2.4f * k)
    line(color, cx + 8f * k, cy + 2f * k, cx + 16f * k, cy + 10f * k, 2.4f * k)
    line(color, cx - 11f * k, cy + 3f * k, cx - 21f * k, cy + 10f * k, 2.4f * k)
    line(color, cx - 10f * k, cy + 3f * k, cx - 18f * k, cy + 14f * k, 2.4f * k)
    // tail
    line(color, cx - 14f * k, cy - 2f * k, cx - 17f * k, cy - 5f * k, 2f * k)
}

/** Vertical traffic light (Germany). */
fun DrawScope.trafficLightVertical(cx: Float, cy: Float, k: Float = 1f) {
    rect(SignColors.Black, cx - 7f * k, cy - 20f * k, cx + 7f * k, cy + 20f * k, 3f * k)
    circle(SignColors.Red, cx, cy - 12f * k, 4.5f * k)
    circle(SignColors.Amber, cx, cy, 4.5f * k)
    circle(SignColors.TrafficGreen, cx, cy + 12f * k, 4.5f * k)
}

/** Horizontal traffic light (Japan: green - yellow - red from left to right). */
fun DrawScope.trafficLightHorizontal(cx: Float, cy: Float, k: Float = 1f) {
    rect(SignColors.Black, cx - 20f * k, cy - 7f * k, cx + 20f * k, cy + 7f * k, 3f * k)
    circle(SignColors.TrafficGreen, cx - 12f * k, cy, 4.5f * k)
    circle(SignColors.Amber, cx, cy, 4.5f * k)
    circle(SignColors.Red, cx + 12f * k, cy, 4.5f * k)
}

/** Two cars-rear with skid marks: "slippery road". */
fun DrawScope.skiddingCar(color: Color, cx: Float, cy: Float, k: Float = 1f, background: Color = SignColors.White) {
    carRear(color, cx, cy - 6f * k, k * 0.9f, background)
    val p1 = Path().apply {
        moveTo(cx - 9f * k, cy + 6f * k)
        cubicTo(cx - 17f * k, cy + 11f * k, cx - 1f * k, cy + 15f * k, cx - 10f * k, cy + 22f * k)
    }
    val p2 = Path().apply {
        moveTo(cx + 9f * k, cy + 6f * k)
        cubicTo(cx + 1f * k, cy + 11f * k, cx + 17f * k, cy + 15f * k, cx + 8f * k, cy + 22f * k)
    }
    drawPath(p1, color, style = Stroke(2.4f * k, cap = StrokeCap.Round))
    drawPath(p2, color, style = Stroke(2.4f * k, cap = StrokeCap.Round))
}

/** Worker digging next to a pile of earth: "roadworks". */
fun DrawScope.roadWorker(color: Color, cx: Float, cy: Float, k: Float = 1f) {
    circle(color, cx - 2f * k, cy - 17f * k, 4f * k)
    line(color, cx - 4f * k, cy - 11f * k, cx - 8f * k, cy + 2f * k, 4f * k) // torso, leaning
    line(color, cx - 8f * k, cy + 2f * k, cx - 14f * k, cy + 15f * k, 3.5f * k)
    line(color, cx - 8f * k, cy + 2f * k, cx - 1f * k, cy + 15f * k, 3.5f * k)
    line(color, cx - 4f * k, cy - 8f * k, cx + 5f * k, cy - 2f * k, 3f * k) // arms
    line(color, cx - 2f * k, cy - 13f * k, cx + 12f * k, cy + 10f * k, 2f * k) // shovel shaft
    poly(color, cx + 10f * k, cy + 8f * k, cx + 16f * k, cy + 9f * k, cx + 15f * k, cy + 14f * k, cx + 11f * k, cy + 13f * k)
    poly(color, cx + 4f * k, cy + 16f * k, cx + 14f * k, cy + 6f * k, cx + 22f * k, cy + 16f * k)
}

/** Falling rocks from a cliff on the left (Japan). */
fun DrawScope.fallingRocks(color: Color) {
    poly(color, 22f, 30f, 38f, 30f, 38f, 74f, 30f, 74f, 30f, 82f, 22f, 70f)
    poly(color, 46f, 38f, 53f, 35f, 56f, 42f, 49f, 45f)
    poly(color, 55f, 52f, 63f, 50f, 64f, 58f, 57f, 59f)
    poly(color, 48f, 62f, 55f, 61f, 57f, 68f, 50f, 70f)
    poly(color, 58f, 70f, 66f, 69f, 66f, 76f, 59f, 77f)
}

/** Steam locomotive (Japanese railway-crossing warning). */
fun DrawScope.steamLocomotive(color: Color, cx: Float, cy: Float, k: Float = 1f, background: Color) {
    rect(color, cx - 20f * k, cy - 6f * k, cx + 6f * k, cy + 5f * k, 2f * k) // boiler
    rect(color, cx + 6f * k, cy - 16f * k, cx + 20f * k, cy + 5f * k, 1f * k) // cab
    rect(background, cx + 9f * k, cy - 13f * k, cx + 17f * k, cy - 7f * k)
    rect(color, cx + 4f * k, cy - 19f * k, cx + 22f * k, cy - 16f * k)
    rect(color, cx - 16f * k, cy - 15f * k, cx - 11f * k, cy - 6f * k) // chimney
    rect(color, cx - 18f * k, cy - 18f * k, cx - 9f * k, cy - 15f * k)
    for (x in floatArrayOf(-15f, -5f, 6f, 15f)) {
        circle(background, cx + x * k, cy + 8f * k, 5f * k)
        circle(color, cx + x * k, cy + 8f * k, 3.8f * k)
    }
    poly(color, cx - 20f * k, cy + 4f * k, cx - 25f * k, cy + 12f * k, cx - 20f * k, cy + 12f * k)
}

/** Two children walking hand in hand (school-zone style). */
fun DrawScope.children(color: Color, cx: Float, cy: Float, k: Float = 1f) {
    person(color, cx - 8f * k, cy + 2f * k, k * 1.05f, facingRight = false)
    person(color, cx + 9f * k, cy + 5f * k, k * 0.8f, facingRight = false)
}

/** Adult and child walking together (pedestrian zone). */
fun DrawScope.adultAndChild(color: Color, cx: Float, cy: Float, k: Float = 1f) {
    person(color, cx - 7f * k, cy, k, facingRight = true)
    person(color, cx + 9f * k, cy + 4f * k, k * 0.72f, facingRight = true)
}
