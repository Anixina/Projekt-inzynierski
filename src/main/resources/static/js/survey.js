window.onload = function(){
    showSurvey();
    showLogoutAndProfile();
};

function showSurvey() {
    var id = sessionStorage.getItem('surveyId');
    var xhr = new XMLHttpRequest();
    var obj;
    var body = "";

    xhr.onreadystatechange = function () {
        if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200) {
            obj = JSON.parse(xhr.responseText);
            body = iterateJSON(obj);
            body = body.substring(0, body.length - 24);
            body += "<button id=\"back\" class=\"btn btn-dark mt-2\" onclick=\"instantRedirectToProfile()\">Back</button>";
            body += "<button id=\"send\" class=\"btn btn-dark mt-2\" onclick=\"sendSurvey()\">Send</button>";
            body += "<div class=\"request-msg-success mt-2\">\n" +
                "                <output class=\"alert alert-success\" role=\"alert\" id=\"msg-success\" name=\"request-msg\"></output>\n" +
                "            </div>\n" +
                "\n" +
                "            <div class=\"request-msg-error mt-2\">\n" +
                "                <output class=\"alert alert-danger\" role=\"alert\" id=\"msg-error\" name=\"request-msg\"></output>\n" +
                "            </div></div>";
            $("body").append(body);

        }
    };
    xhr.open('GET', 'http://localhost:8080/survey/' + id, true);
    xhr.send(null);

}

var buttonType = 0;
var questionID;
var questionIDArray = [];

function iterateJSON(json) {
    var keys = Object.keys(json);
    var body = "<div>";
    var tempid;
    var borderCreated = false;
    for (var i in json) {

        if (i === "id") {
            tempid = json[i];
            if (!keys.includes("answer")) {
                body += "<div class='main-panel border rounded p-4'>";
                borderCreated = true;
            }
        }

        if (i === "type") {
            buttonType = json[i];
        } else if (keys.includes("question") && i === "id") {
            questionID = json[i];
            questionIDArray.push(questionID);
        }

        if (typeof json[i] === 'object') {

            if (borderCreated === true) {
                if (keys.includes("topic")) {
                    body += "</div>";
                    borderCreated = false;
                    body += iterateJSON(json[i]);
                } else {
                    body += iterateJSON(json[i]);
                    body += "</div>";
                    borderCreated = false;
                }
            } else {
                body += iterateJSON(json[i]);
            }

        } else {
            if (i === "answer") {
                if (buttonType === 2) {
                    body += "<input type='radio' name='" + questionID + "' class='" + tempid + "' value='" + json[i] + "'>" +
                        "<label for='" + json[i] + "'>&nbsp;" + json[i] + "</label><br>";
                } else if (buttonType === 3) {
                    body += "<input type='checkbox' name='" + questionID + "' class='" + tempid + "' value='" + json[i] + "'>" +
                        "<label for='" + json[i] + "'>&nbsp;" + json[i] + "</label><br>";
                } else if (buttonType === 1) {
                    body += "<input type='text' class='" + json[i] + "'>" +
                        "<label for='" + json[i] + "'>" + json[i] + "</label><br>";
                }


            } else if (keys.includes("id")) {
                if (i === "topic") {
                    body += "<div class='" + i + " lead'><h3>" + json[i] + "</h3></div>";
                } else if (i === "description") {
                    body += "<div class='" + i + " lead'>" + json[i] + "</div>";
                } else if (i === "question") {
                    body += "<div class='" + i + " mb-2'>" + json[i] + "</div>";
                } else {
                    body += "<div class='" + i + "'>" + json[i] + "</div>";

                }
            }
        }

    }
    body += "</div>";
    return body;
}

function sendSurvey() {

    var checked = [];
    for (var i in questionIDArray) {
        var buttons = document.getElementsByName(questionIDArray[i]);
        for (var j in buttons) {
            if (buttons[j].checked) {
                checked.push(buttons[j].className);
            }
        }
    }
    var responseError = $(".alert.alert-danger");
    var responseSuccess = $(".alert.alert-success");


    for (var id in checked) {

        var xhr = new XMLHttpRequest();
        sendResult(xhr, responseError, responseSuccess);
        xhr.open('POST', 'http://localhost:8080/answer/' + checked[id], false);
        xhr.send(null);

    }


}

function instantRedirectToProfile() {
    window.setTimeout(function () {
        location.href = "profile.html";
    });
}

function sendResult(xhr, responseError, responseSuccess) {
    xhr.onreadystatechange = function () {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status === 200) {
                var json = JSON.parse(xhr.responseText);

                if (json.error) {
                    responseSuccess.hide();
                    responseError.show();
                    responseError.html("Error: " + json.error);
                } else if (json.result) {
                    responseError.hide();
                    responseSuccess.show();
                    responseSuccess.html("Survey send!");
                    setSurveyAsAnswered();
                    redirectToProfile();
                }
            }
        }
    };
}

function setSurveyAsAnswered(){
    var xhr = new XMLHttpRequest();
    var token = $.cookie("token");
    var data = '{"token":"' + token + '","surveyId":"' + sessionStorage.getItem("surveyId") + '"}';
    xhr.open('POST', 'http://localhost:8080/con_us_su/', false);
    xhr.setRequestHeader("Content-Type", "application/json; charset=UTF-8");
    xhr.send(data);
}