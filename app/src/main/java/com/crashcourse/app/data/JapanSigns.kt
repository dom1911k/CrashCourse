package com.crashcourse.app.data

import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import com.crashcourse.app.signs.SignColors.Black
import com.crashcourse.app.signs.SignColors.Blue
import com.crashcourse.app.signs.SignColors.Green
import com.crashcourse.app.signs.SignColors.JpYellow
import com.crashcourse.app.signs.SignColors.Red
import com.crashcourse.app.signs.SignColors.White
import com.crashcourse.app.signs.adultAndChild
import com.crashcourse.app.signs.arrow
import com.crashcourse.app.signs.bentArrow
import com.crashcourse.app.signs.bicycle
import com.crashcourse.app.signs.blueSquare
import com.crashcourse.app.signs.carSide
import com.crashcourse.app.signs.children
import com.crashcourse.app.signs.circle
import com.crashcourse.app.signs.curveArrow
import com.crashcourse.app.signs.deer
import com.crashcourse.app.signs.fallingRocks
import com.crashcourse.app.signs.jpRedTriangle
import com.crashcourse.app.signs.jpWarningDiamond
import com.crashcourse.app.signs.line
import com.crashcourse.app.signs.mandatoryCircle
import com.crashcourse.app.signs.person
import com.crashcourse.app.signs.poly
import com.crashcourse.app.signs.prohibitionCircle
import com.crashcourse.app.signs.prohibitionSlash
import com.crashcourse.app.signs.rect
import com.crashcourse.app.signs.rectOutline
import com.crashcourse.app.signs.roadWorker
import com.crashcourse.app.signs.roundaboutArrows
import com.crashcourse.app.signs.skiddingCar
import com.crashcourse.app.signs.steamLocomotive
import com.crashcourse.app.signs.text
import com.crashcourse.app.signs.trafficLightHorizontal
import com.crashcourse.app.signs.truckSide

/** 50 of the most frequently seen Japanese road signs (道路標識). */
object JapanSigns {

    val all: List<Question> = listOf(
        // ---------------- Regulatory signs (規制標識) ----------------
        signQuestion(
            "jp_s_stop", "一時停止（止まれ）",
            "Stop completely",
            listOf("Slow down", "No stopping", "Stop only if a train is coming"),
            "Stop fully before the stop line, even if the road is clear. Police often check this at quiet junctions.",
        ) { jpRedTriangle("止まれ", "STOP") },

        signQuestion(
            "jp_s_slow", "徐行",
            "Go slowly (so you can stop immediately)",
            listOf("Stop completely", "Give way to buses", "School zone"),
            "'Jokō' means driving slowly enough to stop at once – roughly 10 km/h or less.",
        ) { jpRedTriangle("徐行", "SLOW") },

        signQuestion(
            "jp_s_no_entry", "車両進入禁止",
            "No entry for vehicles",
            listOf("Road closed for pedestrians", "No stopping", "End of expressway"),
            "Vehicles may not enter from this side – typically the exit end of a one-way street.",
        ) {
            circle(White, 50f, 50f, 47f)
            circle(Red, 50f, 50f, 45.5f)
            rect(White, 18f, 43f, 82f, 57f)
        },

        signQuestion(
            "jp_s_closed_vehicles", "車両通行止め",
            "Closed to all vehicles",
            listOf("No parking", "End of restrictions", "Pedestrians only"),
            "No vehicles (cars, motorbikes, bicycles) may pass.",
        ) { prohibitionCircle() },

        signQuestion(
            "jp_s_no_parking", "駐車禁止",
            "No parking (short stops allowed)",
            listOf("No stopping or parking", "Parking allowed", "No entry"),
            "Parking is prohibited, but brief stops (e.g. letting passengers out, loading under 5 minutes) are allowed.",
        ) {
            circle(Red, 50f, 50f, 46f)
            circle(Blue, 50f, 50f, 37f)
            line(Red, 24f, 24f, 76f, 76f, 7f, round = false)
        },

        signQuestion(
            "jp_s_no_stopping", "駐停車禁止",
            "No stopping or parking",
            listOf("No parking only", "Crossroads ahead", "Closed to vehicles"),
            "You may not stop here at all, except when forced by traffic or a signal.",
        ) {
            circle(Red, 50f, 50f, 46f)
            circle(Blue, 50f, 50f, 37f)
            line(Red, 24f, 24f, 76f, 76f, 7f, round = false)
            line(Red, 24f, 76f, 76f, 24f, 7f, round = false)
        },

        signQuestion(
            "jp_s_speed40", "最高速度",
            "Maximum speed 40 km/h",
            listOf("Minimum speed 40 km/h", "Recommended speed 40 km/h", "Road number 40"),
            "Japanese speed limit signs have blue numbers inside a red ring.",
        ) {
            prohibitionCircle()
            text("40", 50f, 52f, 40f, Blue, condensed = true)
        },

        signQuestion(
            "jp_s_min_speed", "最低速度",
            "Minimum speed 50 km/h",
            listOf("Maximum speed 50 km/h", "Speed camera ahead", "50 m to the expressway exit"),
            "You must not drive slower than this – mostly seen on expressways.",
        ) {
            mandatoryCircle()
            text("50", 50f, 46f, 36f, White, condensed = true)
            rect(White, 30f, 64f, 70f, 69f)
        },

        signQuestion(
            "jp_s_no_uturn", "転回禁止",
            "No U-turns",
            listOf("Winding road", "No right turn", "Roundabout ahead"),
            "U-turns are prohibited here.",
        ) {
            prohibitionCircle()
            val p = Path().apply {
                moveTo(60f, 74f)
                lineTo(60f, 42f)
                cubicTo(60f, 26f, 38f, 26f, 38f, 42f)
                lineTo(38f, 56f)
            }
            drawPath(p, Black, style = Stroke(6.5f, cap = StrokeCap.Butt))
            poly(Black, 29f, 54f, 47f, 54f, 38f, 68f)
            prohibitionSlash()
        },

        signQuestion(
            "jp_s_no_pedestrians", "歩行者通行止め",
            "No pedestrians",
            listOf("Pedestrian crossing", "Pedestrians only", "School route"),
            "Pedestrians may not walk here (e.g. tunnels or bypasses).",
        ) {
            prohibitionCircle()
            person(Black, 50f, 56f, 1.15f)
            prohibitionSlash()
        },

        signQuestion(
            "jp_s_no_bicycles", "自転車通行止め",
            "No bicycles",
            listOf("Bicycle lane", "Bicycles only", "Bicycle parking"),
            "Bicycles may not use this road.",
        ) {
            prohibitionCircle()
            bicycle(Black, 50f, 52f, 1.1f)
            prohibitionSlash()
        },

        signQuestion(
            "jp_s_no_trucks", "大型貨物自動車等通行止め",
            "No large trucks",
            listOf("Truck route", "Truck parking", "Trucks must keep left"),
            "Large goods vehicles (and specified heavy vehicles) may not pass.",
        ) {
            prohibitionCircle()
            truckSide(Black, 50f, 53f, 1.05f)
            prohibitionSlash()
        },

        signQuestion(
            "jp_s_height", "高さ制限",
            "Height limit 3.3 m",
            listOf("Tunnel length 3.3 km", "Width limit 3.3 m", "Distance to next exit 3.3 km"),
            "Vehicles (including load) taller than 3.3 m may not pass.",
        ) {
            prohibitionCircle()
            poly(Blue, 44f, 22f, 56f, 22f, 50f, 30f)
            poly(Blue, 44f, 78f, 56f, 78f, 50f, 70f)
            text("3.3m", 50f, 51f, 26f, Blue, condensed = true)
        },

        signQuestion(
            "jp_s_weight", "重量制限",
            "Weight limit 5.5 t (total vehicle weight)",
            listOf("Maximum 5.5 t of cargo for trucks only", "Bridge 5.5 km ahead", "Tunnel clearance 5.5 m"),
            "Vehicles whose total weight (including load) exceeds 5.5 tonnes may not pass – common on bridges.",
        ) {
            prohibitionCircle()
            text("5.5t", 50f, 51f, 30f, Blue, condensed = true)
        },

        signQuestion(
            "jp_s_one_way", "一方通行",
            "One-way traffic",
            listOf("Turn right only", "Priority road", "Detour"),
            "Traffic only in the direction of the arrow. Bicycles may be exempt if a plate says 自転車を除く.",
        ) {
            rect(Blue, 2f, 30f, 98f, 70f, 3f)
            poly(White, 6f, 41f, 70f, 41f, 70f, 34f, 95f, 50f, 70f, 66f, 70f, 59f, 6f, 59f)
            text("一方通行", 39f, 50f, 12f, Blue)
        },

        signQuestion(
            "jp_s_straight_only", "指定方向外進行禁止（直進）",
            "Straight ahead only",
            listOf("One-way street", "Priority road", "Expressway entrance"),
            "Only the shown direction is allowed – no left or right turns here.",
        ) {
            mandatoryCircle()
            arrow(White, 50f, 82f, 50f, 16f, 10f, 20f)
        },

        signQuestion(
            "jp_s_straight_left", "指定方向外進行禁止（直進・左折）",
            "Straight or left turn only (no right turn)",
            listOf("Keep left", "Left lane ends", "Left turn on red allowed"),
            "Japan has no separate 'no right turn' sign – instead it shows the directions you ARE allowed to take.",
        ) {
            mandatoryCircle()
            arrow(White, 58f, 82f, 58f, 16f, 8f, 16f)
            curveArrow(White, 58f, 62f, 58f, 44f, 22f, 44f, 7f, 15f)
        },

        signQuestion(
            "jp_s_roundabout", "環状の交差点における右回り通行",
            "Roundabout – drive clockwise",
            listOf("U-turn permitted", "Multi-storey car park", "Rotating traffic lights"),
            "Japanese roundabouts run clockwise. Vehicles already in the roundabout have priority; indicate left when leaving.",
        ) {
            mandatoryCircle()
            roundaboutArrows(White, clockwise = true)
        },

        signQuestion(
            "jp_s_motor_only", "自動車専用",
            "Motor vehicles only (expressway-type road)",
            listOf("Car park", "Car dealer", "Taxi rank"),
            "Only cars and motorcycles that meet the requirements – no pedestrians, bicycles or mopeds.",
        ) {
            mandatoryCircle()
            carSide(White, 50f, 52f, 1.2f, background = Blue)
        },

        signQuestion(
            "jp_s_bikes_only", "自転車専用",
            "Bicycles only",
            listOf("No bicycles", "Bicycle rental", "Cyclists crossing – warning"),
            "Reserved for bicycles; cars and pedestrians may not use it.",
        ) {
            mandatoryCircle()
            bicycle(White, 50f, 52f, 1.15f)
        },

        signQuestion(
            "jp_s_bikes_peds", "自転車及び歩行者専用",
            "Bicycles and pedestrians only",
            listOf("Park entrance", "Pedestrian crossing", "Shared road with cars"),
            "Cars may not enter. Cyclists using the path must go slowly and give way to pedestrians.",
        ) {
            mandatoryCircle()
            adultAndChild(White, 47f, 33f, 0.6f)
            bicycle(White, 50f, 68f, 0.75f)
        },

        signQuestion(
            "jp_s_peds_only", "歩行者専用",
            "Pedestrians only",
            listOf("School zone – warning", "Pedestrian crossing", "Footbridge"),
            "Vehicles may not enter (often during set hours in shopping streets – check the plate below).",
        ) {
            mandatoryCircle()
            adultAndChild(White, 48f, 52f, 1.1f)
        },

        signQuestion(
            "jp_s_crosswalk", "横断歩道",
            "Pedestrian crossing",
            listOf("Pedestrians only", "No pedestrians", "Pedestrian bridge"),
            "If anyone is about to cross, you MUST stop. No overtaking within 30 m before the crossing.",
        ) {
            blueSquare()
            for (i in 0 until 5) rect(White, 18f + i * 13f, 74f, 25f + i * 13f, 84f)
            person(White, 50f, 50f, 1.15f)
        },

        signQuestion(
            "jp_s_priority", "優先道路",
            "Priority road",
            listOf("Crossroads ahead", "One-way street", "Straight ahead only"),
            "You have priority over traffic joining from side roads.",
        ) {
            blueSquare()
            arrow(White, 50f, 86f, 50f, 16f, 12f, 22f)
            rect(White, 20f, 54f, 80f, 60f)
        },

        signQuestion(
            "jp_s_parking", "駐車可",
            "Parking allowed",
            listOf("Police box", "Petrol station", "Toll gate"),
            "Parking is permitted here (watch for time limits on plates).",
        ) {
            blueSquare()
            text("P", 50f, 51f, 70f, White)
        },

        signQuestion(
            "jp_s_stop_line", "停止線",
            "Stop line (stop here when required)",
            listOf("No stopping", "End of lane", "Toll booth ahead"),
            "Marks where you must stop for signals, stop signs or police signals.",
        ) {
            blueSquare()
            text("停止線", 50f, 40f, 19f, White)
            rect(White, 16f, 62f, 84f, 70f)
        },

        signQuestion(
            "jp_s_lane_dirs", "進行方向別通行区分",
            "Lane directions – choose your lane by direction",
            listOf("Three-lane expressway", "Overtaking lanes", "Detour routes"),
            "Each lane is reserved for the direction shown. Get into the correct lane early; you can't change at the last moment.",
        ) {
            blueSquare()
            curveArrow(White, 27f, 84f, 27f, 46f, 13f, 32f, 5f, 11f)
            arrow(White, 50f, 84f, 50f, 20f, 5f, 11f)
            curveArrow(White, 73f, 84f, 73f, 46f, 87f, 32f, 5f, 11f)
        },

        signQuestion(
            "jp_s_horn", "警笛鳴らせ",
            "Sound your horn",
            listOf("No horn", "Music zone", "Emergency vehicles ahead"),
            "Found on blind mountain bends: you must sound your horn here. Elsewhere the horn should only be used to avoid danger.",
        ) {
            blueSquare()
            poly(White, 22f, 44f, 54f, 44f, 54f, 56f, 22f, 56f)
            poly(White, 50f, 50f, 80f, 26f, 80f, 74f)
            rect(White, 18f, 42f, 24f, 58f, 1.5f)
        },

        signQuestion(
            "jp_s_no_crossing", "車両横断禁止",
            "No right turns across traffic (e.g. into roadside shops)",
            listOf("No U-turns", "Right lane ends", "One-way street to the right"),
            "You may not cross the opposite lane to turn right into a parking lot, shop or side area.",
        ) {
            prohibitionCircle()
            bentArrow(Black, 6.5f, 14f, 42f, 76f, 42f, 46f, 74f, 46f)
            prohibitionSlash()
        },

        // ---------------- Warning signs (警戒標識) ----------------
        signQuestion(
            "jp_s_crossroads", "十形道路交差点あり",
            "Crossroads ahead",
            listOf("Hospital", "Priority road", "Railway crossing"),
            "An intersection is coming. In Japan, if both roads are equal and unsignalled, vehicles from the left have priority.",
        ) {
            jpWarningDiamond()
            rect(Black, 45f, 22f, 55f, 78f)
            rect(Black, 22f, 45f, 78f, 55f)
        },

        signQuestion(
            "jp_s_t_junction", "T形道路交差点あり",
            "T-junction ahead",
            listOf("Dead end", "Priority road ends", "Tunnel ahead"),
            "The road ends at a junction – you'll have to turn left or right.",
        ) {
            jpWarningDiamond()
            rect(Black, 26f, 38f, 74f, 47f)
            rect(Black, 45.5f, 38f, 54.5f, 76f)
        },

        signQuestion(
            "jp_s_y_junction", "Y形道路交差点あり",
            "Y-junction ahead",
            listOf("Lane split for tolls", "Road narrows", "Merging traffic from both sides"),
            "The road splits into two branches ahead.",
        ) {
            jpWarningDiamond()
            rect(Black, 45f, 52f, 55f, 80f)
            line(Black, 50f, 55f, 30f, 28f, 10f, round = false)
            line(Black, 50f, 55f, 70f, 28f, 10f, round = false)
        },

        signQuestion(
            "jp_s_bend_right", "右方屈曲あり",
            "Sharp bend to the right",
            listOf("Turn right only", "Keep right", "Right lane ends"),
            "Slow down before the curve.",
        ) {
            jpWarningDiamond()
            bentArrow(Black, 8f, 16f, 42f, 80f, 42f, 46f, 74f, 46f)
        },

        signQuestion(
            "jp_s_winding", "右つづら折りあり",
            "Winding road (series of bends, first to the right)",
            listOf("Slippery road", "Detour", "Lane changes ahead"),
            "Several tight bends follow – common on Japanese mountain roads.",
        ) {
            jpWarningDiamond()
            bentArrow(Black, 6f, 13f, 40f, 82f, 40f, 68f, 60f, 58f, 40f, 46f, 60f, 34f, 60f, 18f)
        },

        signQuestion(
            "jp_s_railway", "踏切あり",
            "Railway crossing ahead",
            listOf("Train station ahead", "Museum", "Tram lane"),
            "In Japan you must stop before every level crossing (unless a traffic light shows green), look and listen, then cross.",
        ) {
            jpWarningDiamond()
            steamLocomotive(Black, 50f, 54f, 1f, background = JpYellow)
        },

        signQuestion(
            "jp_s_children", "学校、幼稚園、保育所等あり",
            "School / kindergarten nearby – children",
            listOf("Pedestrians only", "Playground parking", "Crosswalk"),
            "Expect children, especially during school-run times. Drive slowly.",
        ) {
            jpWarningDiamond()
            children(Black, 50f, 56f, 1.05f)
        },

        signQuestion(
            "jp_s_signal", "信号機あり",
            "Traffic signals ahead",
            listOf("Railway crossing with lights", "Signals out of order", "Speed camera"),
            "Japanese traffic lights are usually horizontal: green ('ao'), yellow, red from left to right.",
        ) {
            jpWarningDiamond()
            trafficLightHorizontal(50f, 50f, 1.05f)
        },

        signQuestion(
            "jp_s_slippery", "すべりやすい",
            "Slippery road",
            listOf("Drunk driving checkpoint", "Winding road", "Unpaved road ends"),
            "Risk of skidding – especially in rain, snow or ice.",
        ) {
            jpWarningDiamond()
            skiddingCar(Black, 50f, 48f, 1.05f, background = JpYellow)
        },

        signQuestion(
            "jp_s_rocks", "落石のおそれあり",
            "Falling rocks",
            listOf("Steep gradient", "Quarry entrance", "Unpaved road"),
            "Watch for rocks on the road, especially after heavy rain or earthquakes.",
        ) {
            jpWarningDiamond()
            fallingRocks(Black)
        },

        signQuestion(
            "jp_s_narrows", "幅員減少",
            "Road narrows",
            listOf("Bridge", "Two-way traffic", "Lane for buses"),
            "The carriageway becomes narrower ahead.",
        ) {
            jpWarningDiamond()
            val w = 5f
            line(Black, 38f, 74f, 38f, 62f, w); line(Black, 38f, 62f, 44f, 52f, w); line(Black, 44f, 52f, 44f, 26f, w)
            line(Black, 62f, 74f, 62f, 62f, w); line(Black, 62f, 62f, 56f, 52f, w); line(Black, 56f, 52f, 56f, 26f, w)
        },

        signQuestion(
            "jp_s_roadworks", "道路工事中",
            "Roadworks",
            listOf("Farm area", "Snow removal", "Landslide area"),
            "Construction ahead – expect lane closures and flaggers (guide staff waving flags or lights).",
        ) {
            jpWarningDiamond()
            roadWorker(Black, 46f, 52f, 1.0f)
        },

        signQuestion(
            "jp_s_merge", "合流交通あり",
            "Merging traffic from the left",
            listOf("Lane ends", "Y-junction", "Exit on the left"),
            "Traffic joins from the side road – adjust speed and let them merge.",
        ) {
            jpWarningDiamond()
            rect(Black, 50f, 18f, 59f, 82f)
            line(Black, 30f, 72f, 52f, 44f, 6f, round = false)
        },

        signQuestion(
            "jp_s_two_way", "二方向交通",
            "Two-way traffic",
            listOf("One-way street", "Dual carriageway", "Overtaking lane"),
            "Expect oncoming traffic (e.g. after a one-way section). Remember Japan drives on the left.",
        ) {
            jpWarningDiamond()
            arrow(Black, 42f, 78f, 42f, 22f, 6f, 13f)
            arrow(Black, 58f, 22f, 58f, 78f, 6f, 13f)
        },

        signQuestion(
            "jp_s_uphill", "上り急勾配あり",
            "Steep uphill gradient",
            listOf("Steep downhill gradient", "10% of lanes closed", "Ski area"),
            "A steep climb follows. Downhill signs look similar with the slope the other way.",
        ) {
            jpWarningDiamond()
            poly(Black, 24f, 66f, 76f, 66f, 76f, 44f)
            text("10%", 37f, 47f, 11f, Black)
        },

        signQuestion(
            "jp_s_animals", "動物が飛び出すおそれあり",
            "Animals may run onto the road",
            listOf("Zoo", "Hunting area", "Pet-friendly rest area"),
            "Deer, wild boar, raccoon dogs or monkeys may appear. The animal shown varies by region.",
        ) {
            jpWarningDiamond()
            deer(Black, 48f, 56f, 1.05f)
        },

        signQuestion(
            "jp_s_crosswind", "横風注意",
            "Strong crosswinds",
            listOf("Airport nearby", "Fishing port", "Flags – event area"),
            "Gusts may push your car sideways, often on bridges and coastal roads. Hold the wheel firmly and slow down.",
        ) {
            jpWarningDiamond()
            rect(Black, 28f, 24f, 32f, 78f)
            poly(Black, 32f, 30f, 72f, 37f, 72f, 47f, 32f, 54f)
        },

        signQuestion(
            "jp_s_danger", "その他の危険",
            "Other danger",
            listOf("Information", "Emergency phone", "Exclamation zone – no honking"),
            "Warns of a hazard not covered by another sign. A plate underneath often explains it.",
        ) {
            jpWarningDiamond()
            poly(Black, 45f, 24f, 55f, 24f, 52.5f, 62f, 47.5f, 62f)
            circle(Black, 50f, 71f, 5f)
        },

        signQuestion(
            "jp_s_lane_reduction", "車線数減少",
            "Number of lanes decreases",
            listOf("Road narrows on both sides", "Lane for slow vehicles", "Overtaking prohibited"),
            "One lane ends ahead – merge in good time.",
        ) {
            jpWarningDiamond()
            val w = 4.5f
            line(Black, 38f, 72f, 38f, 28f, w)
            line(Black, 62f, 72f, 62f, 60f, w); line(Black, 62f, 60f, 50f, 48f, w); line(Black, 50f, 48f, 50f, 28f, w)
            line(Black, 50f, 72f, 50f, 66f, 2.5f, round = false)
        },

        signQuestion(
            "jp_s_zone30", "ゾーン30",
            "Zone 30 – 30 km/h residential area",
            listOf("30 minutes parking", "Minimum speed 30 km/h", "30 m to school"),
            "Speed is limited to 30 km/h throughout the residential zone to protect pedestrians.",
        ) {
            rect(White, 10f, 10f, 90f, 90f, 4f)
            rectOutline(Black, 12f, 12f, 88f, 88f, 1.2f, 3f)
            circle(Red, 50f, 42f, 26f)
            circle(White, 50f, 42f, 20f)
            text("30", 50f, 43f, 22f, Blue, condensed = true)
            text("ゾーン", 50f, 79f, 14f, Black)
        },

        signQuestion(
            "jp_s_wakaba", "初心運転者標識（若葉マーク）",
            "Beginner driver (licensed less than 1 year)",
            listOf("Eco-friendly car", "Senior driver", "Driving school car"),
            "This mark (shoshinsha mark) must be displayed for one year after getting a licence. You must not cut in front of or tailgate cars showing it. Note: returning to driving after a long break does NOT require it.",
        ) {
            val p = Path().apply {
                moveTo(50f, 92f)
                lineTo(16f, 48f)
                lineTo(16f, 10f)
                lineTo(50f, 36f)
                close()
            }
            drawPath(p, JpYellow)
            val q = Path().apply {
                moveTo(50f, 92f)
                lineTo(84f, 48f)
                lineTo(84f, 10f)
                lineTo(50f, 36f)
                close()
            }
            drawPath(q, Green)
            drawPath(p, Black, style = Stroke(1f))
            drawPath(q, Black, style = Stroke(1f))
        },
    )
}
