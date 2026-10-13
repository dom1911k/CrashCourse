package com.crashcourse.app.data

import com.crashcourse.app.signs.SignColors.Black
import com.crashcourse.app.signs.SignColors.Blue
import com.crashcourse.app.signs.SignColors.Green
import com.crashcourse.app.signs.SignColors.Grey
import com.crashcourse.app.signs.SignColors.PriorityYellow
import com.crashcourse.app.signs.SignColors.Red
import com.crashcourse.app.signs.SignColors.White
import com.crashcourse.app.signs.adultAndChild
import com.crashcourse.app.signs.arrow
import com.crashcourse.app.signs.bicycle
import com.crashcourse.app.signs.blueRect
import com.crashcourse.app.signs.blueSquare
import com.crashcourse.app.signs.carRear
import com.crashcourse.app.signs.carSide
import com.crashcourse.app.signs.children
import com.crashcourse.app.signs.circle
import com.crashcourse.app.signs.cubicArrow
import com.crashcourse.app.signs.curveArrow
import com.crashcourse.app.signs.deer
import com.crashcourse.app.signs.endStripes
import com.crashcourse.app.signs.giveWayTriangle
import com.crashcourse.app.signs.line
import com.crashcourse.app.signs.mandatoryCircle
import com.crashcourse.app.signs.person
import com.crashcourse.app.signs.poly
import com.crashcourse.app.signs.priorityDiamond
import com.crashcourse.app.signs.prohibitionCircle
import com.crashcourse.app.signs.rect
import com.crashcourse.app.signs.rectOutline
import com.crashcourse.app.signs.ring
import com.crashcourse.app.signs.roadWorker
import com.crashcourse.app.signs.roundaboutArrows
import com.crashcourse.app.signs.skiddingCar
import com.crashcourse.app.signs.stopOctagon
import com.crashcourse.app.signs.text
import com.crashcourse.app.signs.trafficLightVertical
import com.crashcourse.app.signs.truckSide
import com.crashcourse.app.signs.warningTriangle

/** 50 of the most frequently seen German road signs (StVO). */
object GermanySigns {

    val all: List<Question> = listOf(
        signQuestion(
            "de_s_stop", "Zeichen 206 – Halt! Vorfahrt gewähren!",
            "Stop completely and give way",
            listOf("Slow down and give way only if traffic is coming", "No stopping on this road", "Police checkpoint ahead"),
            "You must come to a full stop at the stop line (or where you can see the crossing road) and give way – even when nothing is coming.",
        ) { stopOctagon() },

        signQuestion(
            "de_s_give_way", "Zeichen 205 – Vorfahrt gewähren!",
            "Give way to traffic on the road ahead",
            listOf("You have priority at the next junction", "Stop completely, even if the road is clear", "Danger point ahead"),
            "Give way to traffic on the crossing road. You only have to stop if needed.",
        ) { giveWayTriangle() },

        signQuestion(
            "de_s_priority_road", "Zeichen 306 – Vorfahrtstraße",
            "Priority road – you have right of way at junctions",
            listOf("End of priority road", "Give way at the next junction", "Road with a yellow centre line"),
            "You have priority at all junctions along this road until it is cancelled by sign 307 (or a give-way/stop sign).",
        ) { priorityDiamond() },

        signQuestion(
            "de_s_priority_end", "Zeichen 307 – Ende der Vorfahrtstraße",
            "End of priority road",
            listOf("Priority road", "No overtaking for trucks ends", "Priority road continues after a bend"),
            "From here on you no longer have automatic right of way – watch for the next sign or apply 'right before left'.",
        ) {
            priorityDiamond()
            line(Black, 30f, 70f, 70f, 30f, 3f, round = false)
            line(Black, 27f, 67f, 67f, 27f, 1.5f, round = false)
            line(Black, 33f, 73f, 73f, 33f, 1.5f, round = false)
        },

        signQuestion(
            "de_s_priority_next", "Zeichen 301 – Vorfahrt",
            "You have priority at the next junction only",
            listOf("Crossroads – right before left", "Hospital ahead", "Priority road for the rest of the route"),
            "This warning sign gives you right of way at the next intersection or junction only.",
        ) {
            warningTriangle()
            poly(Black, 50f, 34f, 56f, 42f, 55f, 78f, 45f, 78f, 44f, 42f)
            rect(Black, 36f, 56f, 64f, 59f)
        },

        signQuestion(
            "de_s_danger", "Zeichen 101 – Gefahrstelle",
            "General danger point",
            listOf("Information point", "Emergency phone", "No standing"),
            "Warns of a hazard that has no specific sign. Often combined with an additional plate explaining the danger.",
        ) {
            warningTriangle()
            poly(Black, 46f, 40f, 54f, 40f, 52.2f, 66f, 47.8f, 66f)
            circle(Black, 50f, 72.5f, 3.6f)
        },

        signQuestion(
            "de_s_crossroads", "Zeichen 102 – Kreuzung oder Einmündung mit Vorfahrt von rechts",
            "Crossroads ahead – traffic from the right has priority",
            listOf("You have priority at the next crossroads", "Railway crossing ahead", "Hospital or first aid ahead"),
            "The thin cross means 'rechts vor links': vehicles coming from your right have priority.",
        ) {
            warningTriangle()
            rect(Black, 48f, 38f, 52f, 78f)
            rect(Black, 32f, 57f, 68f, 61f)
        },

        signQuestion(
            "de_s_curve_right", "Zeichen 103 – Kurve (rechts)",
            "Bend to the right",
            listOf("Turn right ahead (mandatory)", "Road narrows from the right", "Lane ends on the right"),
            "Warning of a dangerous bend. Reduce speed before the curve.",
        ) {
            warningTriangle()
            curveArrow(Black, 42f, 78f, 42f, 48f, 66f, 47f, 5f, 11f)
        },

        signQuestion(
            "de_s_double_curve", "Zeichen 105 – Doppelkurve (zunächst links)",
            "Double bend, first to the left",
            listOf("Winding road for several kilometres", "Detour to the left", "Lane change to the left"),
            "Two bends in quick succession; the first one turns left.",
        ) {
            warningTriangle()
            cubicArrow(Black, 54f, 79f, 32f, 66f, 36f, 52f, 58f, 41f, 4.5f, 10f)
        },

        signQuestion(
            "de_s_descent", "Zeichen 108 – Gefälle",
            "Steep downhill gradient",
            listOf("Steep uphill gradient", "Maximum 10% of trucks allowed", "Road surface damaged"),
            "The percentage gives the gradient. Use a lower gear to brake with the engine.",
        ) {
            warningTriangle()
            poly(Black, 27f, 53f, 73f, 76f, 27f, 76f)
            text("10%", 59f, 57f, 11f, Black)
        },

        signQuestion(
            "de_s_narrows", "Zeichen 120 – Verengte Fahrbahn",
            "Road narrows",
            listOf("Bridge ahead", "Two-way traffic", "Lane for buses ahead"),
            "The carriageway gets narrower on both sides. Watch out for oncoming vehicles.",
        ) {
            warningTriangle()
            val w = 4f
            line(Black, 36f, 78f, 36f, 64f, w); line(Black, 36f, 64f, 44f, 54f, w); line(Black, 44f, 54f, 44f, 40f, w)
            line(Black, 64f, 78f, 64f, 64f, w); line(Black, 64f, 64f, 56f, 54f, w); line(Black, 56f, 54f, 56f, 40f, w)
        },

        signQuestion(
            "de_s_roadworks", "Zeichen 123 – Arbeitsstelle",
            "Roadworks ahead",
            listOf("Farm vehicles crossing", "Snow clearing in progress", "Loose chippings"),
            "Expect narrowed lanes, workers and construction vehicles. Lower speed limits are common.",
        ) {
            warningTriangle()
            roadWorker(Black, 46f, 62f, 0.85f)
        },

        signQuestion(
            "de_s_lights", "Zeichen 131 – Lichtzeichenanlage",
            "Traffic lights ahead",
            listOf("Level crossing with lights", "Traffic lights are out of order", "Road closed by barrier"),
            "Warns of traffic lights that may come unexpectedly, e.g. after a bend.",
        ) {
            warningTriangle()
            trafficLightVertical(50f, 60f, 0.82f)
        },

        signQuestion(
            "de_s_pedestrians", "Zeichen 133 – Fußgänger",
            "Pedestrians likely – warning",
            listOf("Pedestrian zone begins", "Pedestrian crossing (zebra) – you must stop", "No pedestrians"),
            "Warns of pedestrians on or near the road. It is not itself a zebra crossing.",
        ) {
            warningTriangle()
            person(Black, 50f, 62f, 0.95f, facingRight = false)
        },

        signQuestion(
            "de_s_children", "Zeichen 136 – Kinder",
            "Children – e.g. school or playground nearby",
            listOf("Pedestrian zone", "Footpath only", "Kindergarten parking"),
            "Expect children who may run onto the road unexpectedly. Drive slowly and be ready to brake.",
        ) {
            warningTriangle()
            children(Black, 50f, 63f, 0.8f)
        },

        signQuestion(
            "de_s_cyclists", "Zeichen 138 – Radverkehr",
            "Cyclists crossing or entering the road",
            listOf("Cycle path – mandatory for cyclists", "No cycling", "Bicycle parking"),
            "Warns that cyclists may cross or join the road ahead.",
        ) {
            warningTriangle()
            bicycle(Black, 50f, 67f, 0.72f, rider = true)
        },

        signQuestion(
            "de_s_deer", "Zeichen 142 – Wildwechsel",
            "Wild animals crossing",
            listOf("Hunting area – do not enter", "Zoo or wildlife park ahead", "Farm animals on the road"),
            "Deer and boar often cross here, especially at dawn and dusk. Slow down; if an animal appears, brake hard but don't swerve.",
        ) {
            warningTriangle()
            deer(Black, 48f, 64f, 0.85f)
        },

        signQuestion(
            "de_s_slippery", "Zeichen 114 – Schleuder- oder Rutschgefahr",
            "Slippery road",
            listOf("Winding road", "Drunk drivers – police control", "Road surface ends"),
            "Risk of skidding when wet or dirty. Reduce speed and avoid sudden steering or braking.",
        ) {
            warningTriangle()
            skiddingCar(Black, 50f, 60f, 0.95f)
        },

        signQuestion(
            "de_s_two_way", "Zeichen 125 – Gegenverkehr",
            "Two-way traffic ahead",
            listOf("One-way street", "Give way to oncoming traffic", "Dual carriageway begins"),
            "After a section of one-way traffic (or a dual carriageway), expect oncoming traffic again.",
        ) {
            warningTriangle()
            arrow(Black, 43f, 40f, 43f, 78f, 4.5f, 10f)
            arrow(Black, 57f, 78f, 57f, 40f, 4.5f, 10f)
        },

        signQuestion(
            "de_s_rail_gate", "Zeichen 151 – Bahnübergang",
            "Railway crossing with barriers ahead",
            listOf("Cattle grid", "Road closed by fence", "Bridge with railing"),
            "A level crossing with gates/barriers is ahead. Countdown beacons (three, two, one stripe) often follow.",
        ) {
            warningTriangle()
            rect(Black, 30f, 56f, 70f, 60f)
            rect(Black, 30f, 66f, 70f, 70f)
            rect(Black, 31f, 50f, 35f, 77f)
            rect(Black, 48f, 50f, 52f, 77f)
            rect(Black, 65f, 50f, 69f, 77f)
        },

        signQuestion(
            "de_s_andreas", "Zeichen 201 – Andreaskreuz",
            "Railway crossing – trains have priority",
            listOf("Crossroads – right before left", "No entry", "Hospital"),
            "Trains always have priority. Stop if lights flash red or barriers are lowering.",
        ) {
            line(Red, 14f, 26f, 86f, 74f, 17f, round = false)
            line(Red, 14f, 74f, 86f, 26f, 17f, round = false)
            line(White, 18f, 28.7f, 82f, 71.3f, 10f, round = false)
            line(White, 18f, 71.3f, 82f, 28.7f, 10f, round = false)
            line(Red, 22f, 31.3f, 32f, 38f, 10f, round = false)
            line(Red, 68f, 62f, 78f, 68.7f, 10f, round = false)
            line(Red, 22f, 68.7f, 32f, 62f, 10f, round = false)
            line(Red, 68f, 38f, 78f, 31.3f, 10f, round = false)
        },

        signQuestion(
            "de_s_no_vehicles", "Zeichen 250 – Verbot für Fahrzeuge aller Art",
            "No vehicles of any kind",
            listOf("No stopping", "End of all restrictions", "Road for motor vehicles only"),
            "No vehicles at all may enter (bicycles may be pushed). Additional plates often allow exceptions, e.g. 'Anlieger frei'.",
        ) { prohibitionCircle() },

        signQuestion(
            "de_s_no_entry", "Zeichen 267 – Verbot der Einfahrt",
            "No entry",
            listOf("No stopping", "Road closed for trucks", "Dead end"),
            "Do not enter – typically the wrong end of a one-way street.",
        ) {
            circle(White, 50f, 50f, 47f)
            circle(Red, 50f, 50f, 45.5f)
            rect(White, 18f, 43f, 82f, 57f)
        },

        signQuestion(
            "de_s_no_overtaking", "Zeichen 276 – Überholverbot für Kraftfahrzeuge aller Art",
            "No overtaking of motor vehicles",
            listOf("Two-lane traffic", "Keep your distance", "No overtaking for trucks only"),
            "You must not overtake multi-track motor vehicles. Overtaking single-track vehicles (e.g. bicycles) is still allowed.",
        ) {
            prohibitionCircle()
            carRear(Red, 36f, 55f, 0.82f)
            carRear(Black, 64f, 55f, 0.82f)
        },

        signQuestion(
            "de_s_speed30", "Zeichen 274 – Zulässige Höchstgeschwindigkeit",
            "Maximum speed 30 km/h",
            listOf("Minimum speed 30 km/h", "Recommended speed 30 km/h", "Keep 30 m distance"),
            "Maximum speed limit. Valid until cancelled or until the next junction-related sign change.",
        ) {
            prohibitionCircle()
            text("30", 50f, 51f, 38f, Black, condensed = true)
        },

        signQuestion(
            "de_s_speed_end", "Zeichen 278 – Ende der zulässigen Höchstgeschwindigkeit",
            "End of the 60 km/h speed limit",
            listOf("Minimum speed 60 km/h", "Speed limit 60 km/h starts", "60 km/h recommended in wet conditions"),
            "The previous speed limit ends – the general limit applies again (e.g. 100 km/h outside built-up areas).",
        ) {
            circle(White, 50f, 50f, 46f)
            ring(Black, 50f, 50f, 45f, 1.5f)
            text("60", 50f, 51f, 38f, Grey, condensed = true)
            endStripes(count = 5, spacing = 6f, r = 40f, w = 1.6f)
        },

        signQuestion(
            "de_s_end_all", "Zeichen 282 – Ende sämtlicher streckenbezogener Geschwindigkeitsbeschränkungen und Überholverbote",
            "End of all speed limits and overtaking restrictions",
            listOf("No vehicles of any kind", "End of built-up area", "Road closed"),
            "All previous speed limits and no-overtaking signs end. On a German Autobahn this often means no general speed limit (130 km/h recommended).",
        ) {
            circle(White, 50f, 50f, 46f)
            ring(Black, 50f, 50f, 45f, 1.5f)
            endStripes(count = 7, spacing = 7.5f, r = 41f, w = 2.4f)
        },

        signQuestion(
            "de_s_min_speed", "Zeichen 275 – Vorgeschriebene Mindestgeschwindigkeit",
            "Minimum speed 30 km/h",
            listOf("Maximum speed 30 km/h", "Zone 30 begins", "Recommended speed 30 km/h"),
            "You must not drive slower than this (unless road or traffic conditions require it).",
        ) {
            mandatoryCircle()
            text("30", 50f, 51f, 38f, White, condensed = true)
        },

        signQuestion(
            "de_s_no_stopping", "Zeichen 283 – Absolutes Haltverbot",
            "No stopping at all",
            listOf("No parking, short stops allowed", "No entry", "End of parking zone"),
            "Stopping is forbidden on this side of the road – not even to let a passenger out.",
        ) {
            circle(Red, 50f, 50f, 46f)
            circle(Blue, 50f, 50f, 37f)
            line(Red, 24f, 24f, 76f, 76f, 7f, round = false)
            line(Red, 24f, 76f, 76f, 24f, 7f, round = false)
        },

        signQuestion(
            "de_s_restricted_stopping", "Zeichen 286 – Eingeschränktes Haltverbot",
            "No parking (stopping up to 3 minutes / for loading allowed)",
            listOf("No stopping at all", "Parking for residents only", "One-way street"),
            "You may stop briefly (max. 3 minutes) or to load/unload or let passengers in/out, but not park.",
        ) {
            circle(Red, 50f, 50f, 46f)
            circle(Blue, 50f, 50f, 37f)
            line(Red, 24f, 24f, 76f, 76f, 7f, round = false)
        },

        signQuestion(
            "de_s_one_way", "Zeichen 220 – Einbahnstraße",
            "One-way street (in the direction of the arrow)",
            listOf("Turn right ahead", "Detour", "Priority over oncoming traffic"),
            "Traffic may only flow in the direction of the arrow. Cyclists may be allowed the other way with an additional sign.",
        ) {
            rect(Blue, 2f, 32f, 98f, 68f, 3f)
            poly(White, 6f, 42f, 74f, 42f, 74f, 36f, 95f, 50f, 74f, 64f, 74f, 58f, 6f, 58f)
            text("Einbahnstraße", 42f, 50f, 9f, Blue, condensed = true)
        },

        signQuestion(
            "de_s_turn_right", "Zeichen 209 – Vorgeschriebene Fahrtrichtung rechts",
            "Turn right (mandatory)",
            listOf("Bend to the right", "Keep right", "One-way street to the right"),
            "You must turn right here.",
        ) {
            mandatoryCircle()
            curveArrow(White, 40f, 80f, 40f, 44f, 76f, 44f, 9f, 18f)
        },

        signQuestion(
            "de_s_straight_right", "Zeichen 214 – Vorgeschriebene Fahrtrichtung geradeaus oder rechts",
            "Go straight ahead or turn right only",
            listOf("No turning right", "Lane for right turns only", "Priority road bends right"),
            "Turning left is not permitted at the next junction.",
        ) {
            mandatoryCircle()
            arrow(White, 42f, 82f, 42f, 16f, 8f, 16f)
            curveArrow(White, 42f, 62f, 42f, 44f, 78f, 44f, 7f, 15f)
        },

        signQuestion(
            "de_s_keep_right", "Zeichen 222 – Rechts vorbei",
            "Pass on the right",
            listOf("Turn right ahead", "Road bends right", "Right lane ends"),
            "Pass the obstacle or traffic island on the right side.",
        ) {
            mandatoryCircle()
            arrow(White, 30f, 30f, 70f, 70f, 9f, 20f)
        },

        signQuestion(
            "de_s_roundabout", "Zeichen 215 – Kreisverkehr",
            "Roundabout – drive anticlockwise",
            listOf("U-turn allowed", "Car park with circular ramp", "Traffic lights ahead"),
            "Traffic inside the roundabout has priority (combined with sign 205). Don't indicate when entering, indicate right when leaving.",
        ) {
            mandatoryCircle()
            roundaboutArrows(White, clockwise = false)
        },

        signQuestion(
            "de_s_cycle_path", "Zeichen 237 – Radweg",
            "Cycle path (mandatory for cyclists)",
            listOf("Cyclists crossing – warning", "Bike rental", "No cycling"),
            "Only for bicycles; cyclists must use it. Cars must not drive or park on it.",
        ) {
            mandatoryCircle()
            bicycle(White, 50f, 52f, 1.15f)
        },

        signQuestion(
            "de_s_shared_path", "Zeichen 240 – Gemeinsamer Geh- und Radweg",
            "Shared path for pedestrians and cyclists",
            listOf("Separate paths for pedestrians and cyclists", "Pedestrian zone – cycling allowed at night", "Bus and bike lane"),
            "Pedestrians and cyclists share the path. Cyclists must use it and take care of pedestrians.",
        ) {
            mandatoryCircle()
            adultAndChild(White, 48f, 31f, 0.62f)
            line(White, 8f, 50f, 92f, 50f, 2.5f, round = false)
            bicycle(White, 50f, 67f, 0.8f)
        },

        signQuestion(
            "de_s_bike_street", "Zeichen 244.1 – Beginn einer Fahrradstraße",
            "Bicycle street – cyclists have priority, max 30 km/h",
            listOf("Cycle path – cars may not cross", "Bicycle parking zone", "Start of a cycle route network"),
            "Other vehicles only if allowed by an additional sign; max 30 km/h, and cyclists may ride side by side.",
        ) {
            rect(White, 14f, 4f, 86f, 96f, 3f)
            rectOutline(Black, 16f, 6f, 84f, 94f, 1.2f, 2f)
            circle(Blue, 50f, 38f, 28f)
            bicycle(White, 50f, 40f, 0.8f)
            text("Fahrrad-", 50f, 76f, 9f, Black, condensed = true)
            text("straße", 50f, 86f, 9f, Black, condensed = true)
        },

        signQuestion(
            "de_s_zebra", "Zeichen 350 – Fußgängerüberweg",
            "Pedestrian (zebra) crossing",
            listOf("Pedestrians likely – warning", "Pedestrian zone", "Footpath only"),
            "You must let pedestrians cross who are on or want to use the crossing. No overtaking and no parking within 5 m before it.",
        ) {
            blueSquare()
            poly(White, 50f, 14f, 86f, 82f, 14f, 82f)
            for (i in 0 until 4) rect(Black, 30f + i * 11f, 72f, 35f + i * 11f, 78f)
            person(Black, 52f, 52f, 0.95f, facingRight = true)
        },

        signQuestion(
            "de_s_parking", "Zeichen 314 – Parken",
            "Parking area",
            listOf("Police station", "Petrol station", "Pick-up point for taxis"),
            "Parking is allowed here; additional plates may limit time or require a parking disc/ticket.",
        ) {
            blueSquare()
            text("P", 50f, 51f, 70f, White)
        },

        signQuestion(
            "de_s_autobahn", "Zeichen 330.1 – Autobahn",
            "Motorway (Autobahn) begins",
            listOf("Dual carriageway with bridge", "Tunnel ahead", "Motor-vehicle road (Kraftfahrstraße)"),
            "Only motor vehicles that can go faster than 60 km/h. No turning, reversing or stopping on the carriageway.",
        ) {
            blueSquare()
            poly(White, 18f, 86f, 44f, 86f, 47f, 48f, 41f, 48f)
            poly(White, 82f, 86f, 56f, 86f, 53f, 48f, 59f, 48f)
            rect(White, 18f, 30f, 82f, 40f, 2f)
            rect(White, 22f, 40f, 28f, 48f)
            rect(White, 72f, 40f, 78f, 48f)
        },

        signQuestion(
            "de_s_autobahn_end", "Zeichen 330.2 – Ende der Autobahn",
            "End of motorway",
            listOf("Motorway closed", "Motorway exit 1 km ahead", "No motorway for trucks"),
            "Normal road rules apply again: watch for the new speed limit and possible oncoming traffic.",
        ) {
            blueSquare()
            poly(White, 18f, 86f, 44f, 86f, 47f, 48f, 41f, 48f)
            poly(White, 82f, 86f, 56f, 86f, 53f, 48f, 59f, 48f)
            rect(White, 18f, 30f, 82f, 40f, 2f)
            rect(White, 22f, 40f, 28f, 48f)
            rect(White, 72f, 40f, 78f, 48f)
            line(Red, 14f, 86f, 86f, 14f, 7f, round = false)
        },

        signQuestion(
            "de_s_kraftfahrstrasse", "Zeichen 331.1 – Kraftfahrstraße",
            "Motor-vehicle road (expressway rules apply)",
            listOf("Car park", "Car wash", "Car-sharing station"),
            "Like a motorway: only for motor vehicles faster than 60 km/h, no stopping, and you may only enter/leave at junctions.",
        ) {
            blueSquare()
            carSide(White, 50f, 52f, 1.3f, background = Blue)
        },

        signQuestion(
            "de_s_play_street", "Zeichen 325.1 – Verkehrsberuhigter Bereich",
            "Traffic-calmed area – walking speed only",
            listOf("Playground ahead", "Residential parking only", "Zone 30"),
            "Drive at walking speed (approx. 4–7 km/h), children may play on the street, and parking is only allowed in marked spaces.",
        ) {
            blueRect(14f, 86f)
            // house
            rect(White, 12f, 44f, 34f, 70f)
            poly(White, 9f, 46f, 23f, 30f, 37f, 46f)
            rect(Blue, 20f, 56f, 26f, 70f)
            // adult with child playing ball
            person(White, 50f, 58f, 0.7f, facingRight = true)
            person(White, 63f, 64f, 0.5f, facingRight = false)
            circle(White, 70f, 72f, 2.5f)
            // car
            carSide(White, 80f, 70f, 0.42f, background = Blue)
            line(White, 10f, 78f, 90f, 78f, 1.5f, round = false)
        },

        signQuestion(
            "de_s_town", "Zeichen 310 – Ortstafel (Vorderseite)",
            "Built-up area begins – speed limit 50 km/h",
            listOf("Tourist information for the town", "Town centre parking", "Start of a 30 km/h zone"),
            "The yellow town sign itself sets the 50 km/h limit for built-up areas – no separate speed sign is needed.",
        ) {
            rect(PriorityYellow, 4f, 24f, 96f, 76f, 3f)
            rectOutline(Black, 7f, 27f, 93f, 73f, 2f, 2f)
            text("Musterstadt", 50f, 46f, 15f, Black, condensed = true)
            text("Landkreis Beispiel", 50f, 62f, 8f, Black, condensed = true)
        },

        signQuestion(
            "de_s_no_trucks", "Zeichen 253 – Verbot für Kraftfahrzeuge über 3,5 t",
            "No trucks (motor vehicles over 3.5 t)",
            listOf("Trucks must use this lane", "Truck parking", "Truck traffic – warning"),
            "Goods vehicles over 3.5 t are prohibited (buses and cars are allowed).",
        ) {
            prohibitionCircle()
            truckSide(Black, 50f, 53f, 1.1f)
        },

        signQuestion(
            "de_s_recommended", "Zeichen 380 – Richtgeschwindigkeit",
            "Recommended speed 130 km/h",
            listOf("Maximum speed 130 km/h", "Minimum speed 130 km/h", "Exit in 130 m"),
            "Not a limit but a recommendation – it is the advisory speed on Autobahns without a posted limit.",
        ) {
            blueSquare()
            text("130", 50f, 51f, 36f, White, condensed = true)
        },

        signQuestion(
            "de_s_zone30", "Zeichen 274.1 – Beginn einer Tempo 30-Zone",
            "Zone 30 begins",
            listOf("Minimum speed 30 km/h in this zone", "Parking zone for 30 minutes", "30 km/h only for trucks"),
            "Maximum 30 km/h throughout the zone. Inside, 'right before left' usually applies at junctions.",
        ) {
            rect(White, 10f, 10f, 90f, 90f, 4f)
            rectOutline(Black, 12f, 12f, 88f, 88f, 1.2f, 3f)
            circle(Red, 50f, 42f, 26f)
            circle(White, 50f, 42f, 20f)
            text("30", 50f, 43f, 22f, Black, condensed = true)
            text("ZONE", 50f, 79f, 15f, Black)
        },

        signQuestion(
            "de_s_bus_stop", "Zeichen 224 – Haltestelle",
            "Bus or tram stop",
            listOf("Hotel", "Parking for hybrid cars", "Helicopter landing site"),
            "No parking within 15 m before and after the sign. Pass buses at a stop carefully; with hazard lights on, only at walking speed.",
        ) {
            circle(Green, 50f, 50f, 46f)
            circle(PriorityYellow, 50f, 50f, 38f)
            text("H", 50f, 51f, 50f, Green)
        },

        signQuestion(
            "de_s_dead_end", "Zeichen 357 – Sackgasse",
            "Dead end (no through road)",
            listOf("Priority road ends", "No entry", "Hospital"),
            "The road does not continue for vehicles. Additional symbols may show that pedestrians or cyclists can pass.",
        ) {
            blueSquare()
            rect(White, 42f, 34f, 58f, 86f)
            rect(Red, 22f, 18f, 78f, 34f)
        },
    )
}
