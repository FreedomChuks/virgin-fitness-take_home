package com.virginactive.core.ui.preview

import com.virginactive.core.model.*
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

object PreviewData {
    val fitnessClass =
        FitnessClass(
            ClassRef("preview-club", "preview-yoga"),
            "Deep Stretch Yoga",
            "Lerato Molefe",
            "yoga",
            OffsetDateTime.parse("2035-05-15T07:00:00+02:00"),
            OffsetDateTime.parse("2035-05-15T08:00:00+02:00"),
            "Africa/Johannesburg",
            20,
            8,
            Availability.AVAILABLE,
            BookingStatus.NONE,
            0,
        )
    val bookedClass = fitnessClass.copy(available = 7, bookingStatus = BookingStatus.BOOKED)
    val fullClass =
        fitnessClass.copy(
            ref = ClassRef("preview-club", "preview-hiit"),
            title = "HIIT Lab",
            trainer = "Sipho Dlamini",
            category = "hiit",
            available = 0,
            availability = Availability.FULL,
            waitlistCount = 2)
    val waitlistedClass =
        fullClass.copy(bookingStatus = BookingStatus.WAITLISTED, waitlistCount = 3)
    val cancelledClass =
        fitnessClass.copy(
            ref = ClassRef("preview-club", "preview-swim"),
            title = "Lane Swim",
            category = "swimming",
            availability = Availability.CANCELLED)
    val finishedClass =
        fitnessClass.copy(
            ref = ClassRef("preview-club", "preview-past"),
            startsAt = OffsetDateTime.parse("2020-05-15T07:00:00+02:00"),
            endsAt = OffsetDateTime.parse("2020-05-15T08:00:00+02:00"))
    val longTitleClass =
        fitnessClass.copy(
            ref = ClassRef("preview-club", "preview-long"),
            title = "Restorative Yoga and Mobility Flow for Runners and Cyclists",
            category = "groupWorkout")

    val bookedSoonClass: FitnessClass
        get() {
            val start =
                OffsetDateTime.now(ZoneOffset.ofHours(2)).plusHours(3).truncatedTo(ChronoUnit.HOURS)
            return bookedClass.copy(startsAt = start, endsAt = start.plusMinutes(45))
        }

    val table =
        Timetable(
            "preview-club",
            LocalDate.parse("2035-05-14"),
            LocalDate.parse("2035-05-20"),
            LocalDate.parse("2035-05-15"),
            listOf(
                TimetableDay(LocalDate.parse("2035-05-14"), listOf(cancelledClass)),
                TimetableDay(
                    LocalDate.parse("2035-05-15"),
                    listOf(
                        fitnessClass,
                        bookedClass.copy(
                            ref = ClassRef("preview-club", "preview-spin"),
                            title = "RPM Spin",
                            trainer = "Thabo Ndlovu",
                            category = "spin",
                            startsAt = OffsetDateTime.parse("2035-05-15T12:00:00+02:00"),
                            endsAt = OffsetDateTime.parse("2035-05-15T12:45:00+02:00")),
                        fullClass.copy(
                            startsAt = OffsetDateTime.parse("2035-05-15T17:30:00+02:00"),
                            endsAt = OffsetDateTime.parse("2035-05-15T18:00:00+02:00")),
                        longTitleClass.copy(
                            startsAt = OffsetDateTime.parse("2035-05-15T19:00:00+02:00"),
                            endsAt = OffsetDateTime.parse("2035-05-15T20:00:00+02:00")))),
                TimetableDay(LocalDate.parse("2035-05-16"), emptyList()),
                TimetableDay(LocalDate.parse("2035-05-17"), listOf(waitlistedClass)),
                TimetableDay(LocalDate.parse("2035-05-18"), emptyList()),
                TimetableDay(LocalDate.parse("2035-05-19"), emptyList()),
                TimetableDay(LocalDate.parse("2035-05-20"), emptyList()),
            ))
    val home =
        listOf(
            HomeBlock.Greeting("Good morning, Avid"),
            HomeBlock.Hero(
                "Crush your goals this week",
                "You've attended 3 classes — 1 more to hit your weekly target."),
            HomeBlock.MyClub(
                "Virgin Active Sea Point",
                "96 Beach Rd, Sea Point, Cape Town, 8005",
                "05:00 – 22:00",
                "+27 21 439 1240"),
            HomeBlock.Tiles(
                TileKind.CLASSES,
                "Your week",
                listOf(
                    HomeTile(
                        "yoga",
                        fitnessClass.title,
                        fitnessClass.trainer,
                        "Booked",
                        fitnessClass.startsAt,
                        "View",
                        HomeAction.OpenClass(fitnessClass.ref)),
                    HomeTile(
                        "hiit",
                        fullClass.title,
                        fullClass.trainer,
                        "Waitlisted",
                        fullClass.startsAt,
                        "View",
                        HomeAction.OpenClass(fullClass.ref)),
                ),
                HomeAction.OpenTimetable("preview-club")),
            HomeBlock.Tiles(
                TileKind.REWARDS,
                "Your rewards",
                listOf(
                    HomeTile(
                        "smoothie",
                        "Free smoothie at the juice bar",
                        "Show this at any Virgin Active café",
                        "Expires Sunday",
                        null,
                        null,
                        null),
                    HomeTile(
                        "retail",
                        "20% off retail at any club",
                        "Valid on apparel and accessories",
                        null,
                        null,
                        null,
                        null)),
                null),
            HomeBlock.Promotion(
                "Summer Shape-Up Challenge",
                "Join 12 classes in 4 weeks and earn bonus rewards. Starts 1 June."),
        )
}
