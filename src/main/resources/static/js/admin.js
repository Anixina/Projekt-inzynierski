window.onload = function () {
    showUsers();
    showLogoutAndProfile();
};

function showUsers() {
    var xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function () {
        if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200) {
            var json = JSON.parse(xhr.responseText);

            var body = "<table class=\"table\">\n" +
                "  <thead class = 'thead-dark'>\n" +
                "    <tr>\n" +
                "      <th scope=\"col\">#</th>\n" +
                "      <th scope=\"col\">Email</th>\n" +
                "      <th scope=\"col\">Survey</th>\n" +
                "      <th scope=\"col\">Admin</th>\n" +
                "      <th scope=\"col\">Send</th>\n" +
                "    </tr>\n" +
                "  </thead><tbody>";

            for (var i = 0; i < json.length; i++) {
                var iter = i + 1;
                body += "<tr>\n" +
                    "      <th scope=\"row\">" + iter + "</th>";
                var obj = json[i];

                body += "<td>" + obj["email"] + "</td><td>";
                body += getAllSurveys(i);
                body += "</td>";
                body += "<td><div class=\"form-check d-flex justify-content-center\">";
                if (obj["group"] === "admin") {
                    body += "<input id='input" + i + "' class=\"form-check-input\" type=\"checkbox\" value=\"\" checked>";
                } else {
                    body += "<input id='input" + i + "' class=\"form-check-input\" type=\"checkbox\" value=\"\">";
                }
                body += "</div></td>";
                body += "<td><button id=\"send\" class=\"btn btn-dark\" onclick=\"sendForm(" + i + "," + obj["id"] + ")\">Update</button></td>";

                body += "</tr>";

            }
            body += "</tbody></table>";
            $(".main-panel").html(body);

        }
    };
    xhr.open('GET', 'http://localhost:8080/users/all', false);
    xhr.send(null);

}

function getAllSurveys(i) {
    var body = "";
    var xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function () {
        if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200) {
            var surveys = JSON.parse(xhr.responseText);
            body += "<select id='sel" + i + "' class=\"form-control\">";
            body += "<option></option>";
            for (var s in surveys) {
                var survey = surveys[s];
                body += "<option value='" + survey["id"] + "'>" + survey["topic"] + "</option>";
            }
            body += "</select>";
        }
    };
    xhr.open('GET', 'http://localhost:8080/survey/all', false);
    xhr.send(null);
    return body;
}

function sendForm(i, id) {

    var surveyId = $("#sel" + i).children("option:selected").val();

    if (surveyId !== "") {
        addSurveyToUser(id, surveyId);
    }

    var group;
    if ($("#input" + i).prop("checked")) {
        group = "admin";
    } else {
        group = "user";
    }
    var body = '{"id":"' + id + '", "group":"' + group + '"}';
    updateAdmin(body);


}

function updateAdmin(body) {
    var xhr = new XMLHttpRequest();
    xhr.open('POST', 'http://localhost:8080/admin', false);
    xhr.setRequestHeader("Content-Type", "application/json; charset=UTF-8");
    xhr.send(body);


}

function addSurveyToUser(uId, sId) {
    var body = '{"uId":"' + uId + '", "sId":"' + sId + '"}';
    var xhr = new XMLHttpRequest();

    xhr.onreadystatechange = function () {
        if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200) {
            redirectToAdminPanel();
        }
    };

    xhr.open('POST', 'http://localhost:8080/surveyToUser', false);
    xhr.setRequestHeader("Content-Type", "application/json; charset=UTF-8");
    xhr.send(body);
}

function redirectToAdminPanel() {
    window.setTimeout(function () {
        location.href = "admin.html";
    }, 1000);
}

function getSurveys(xhr) {
    xhr.open('GET', 'http://localhost:8080/survey/all', false);
    xhr.send(null);
}