package com.pezont.teammates.fake

import com.pezont.teammates.models.Questionnaire

data class FakeDataStore(
    var i: Int
) {

    val questionnaireList = listOf(
        Questionnaire(
            "l",
            "h,",
            "ertyu",
            7,
            "1",
            "qwe"
        ),

        Questionnaire(
            "k",
            "Wanna find teammate Dota 3",
            "zxcvb description",
            8,
            "3",
            "https://dummyjson.com/icon/emilys/128"


        ),
        Questionnaire(
            "r",
            "Wanna find teammate Dota 4",
            "zxcvb description",
            8,
            "4",
            "https://dummyjson.com/icon/emilys/128"


        ),
        Questionnaire(
            "r",
            "Wanna find teammate Dota 5",
            "zxcvb description",
            8,
            "5",
            "https://dummyjson.com/icon/emilys/128"


        ),
    )


    val questionnaireList2 = listOf(
        Questionnaire(
            header = "Questionnaire ${i} - Team Finding",
            game = "Dota 2",
            description = "Looking for teammates for Dota 2. ID: ${i}001",
            authorId = 25,
            questionnaireId = "${i}001",
            imagePath = "https://dummyjson.com/icon/emilys/128"
        ),
        Questionnaire(
            header = "Questionnaire ${i} - Strategy Enthusiast",
            game = "Dota 3",
            description = "Searching for strategy game players. ID: ${i}002",
            authorId = 30,
            questionnaireId = "${i}002",
            imagePath = "https://dummyjson.com/icon/michaelw/128"
        ),
        Questionnaire(
            header = "Questionnaire $i - Competitive Play",
            game = "Dota 4",
            description = "Join my team for competitive Dota 4. ID: ${i}003",
            authorId = 20,
            questionnaireId = "${i}003",
            imagePath = "https://dummyjson.com/icon/emilys/128"
        ),
        Questionnaire(
            header = "Questionnaire ${i} - Beginner Players",
            game = "Dota 5",
            description = "Looking for beginners to play Dota 5. ID: ${i}004",
            authorId = 18,
            questionnaireId = "${i}004",
            imagePath = "https://dummyjson.com/icon/sophiab/128"
        ),
        Questionnaire(
            header = "Questionnaire ${i} - Advanced Gamers",
            game = "Dota 6",
            description = "Join my Dota 6 team for ranked games. ID: ${i}005",
            authorId = 28,
            questionnaireId = "${i}005",
            imagePath = "https://dummyjson.com/icon/oliviaw/128"
        ),
        Questionnaire(
            header = "Questionnaire ${i} - Dota Strategists",
            game = "Dota 7",
            description = "Looking for skilled players for Dota 7. ID: ${i}006",
            authorId = 35,
            questionnaireId = "${i}006",
            imagePath = "https://dummyjson.com/icon/alexanderj/128"
        ),
        Questionnaire(
            header = "Questionnaire ${i} - Friendly Community",
            game = "Dota 8",
            description = "Join a friendly Dota 8 gaming community. ID: ${i}007",
            authorId = 22,
            questionnaireId = "${i}007",
            imagePath = "https://dummyjson.com/icon/emilys/128"
        ),
        Questionnaire(
            header = "Questionnaire ${i} - Cooperative Play",
            game = "Dota 9",
            description = "Looking for cooperative players for Dota 9. ID: ${i}008",
            authorId = 26,
            questionnaireId = "${i}008",
            imagePath = "https://dummyjson.com/icon/emilys/128"
        ),
        Questionnaire(
            header = "Questionnaire ${i} - Competitive Gamers",
            game = "Dota 10",
            description = "Join my Dota 10 team for tournaments. ID: ${i}009",
            authorId = 32,
            questionnaireId = "${i}009",
            imagePath = "https://dummyjson.com/icon/emilys/128"
        ),
        Questionnaire(
            header = "Questionnaire ${i} - Casual Players",
            game = "Dota 11",
            description = "Looking for casual players for Dota 11. ID: ${i}010",
            authorId = 24,
            questionnaireId = "${i}010",
            imagePath = "https://dummyjson.com/icon/isabellad/128"
        )
    )


}
