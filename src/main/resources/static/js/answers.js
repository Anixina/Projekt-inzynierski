window.onload = function () {
    getAnswers();
    showLogoutAndProfile();
};


let answerId = 0;
let answerName = "";
let qIdArr = [];

function getAnswers() {
    let key = sessionStorage.getItem('key');
    sessionStorage.removeItem('key');
    let body = "";
    let xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function () {
        if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200) {
            let answers = JSON.parse(xhr.responseText);

            if ($.isEmptyObject(answers)) {
                let error = `<div class="request-msg-error main-panel border rounded">
                                    <div class="alert alert-danger mb-0" role="alert" id="msg-error">Invalid key!</div>
                                </div>`;
                $('body').append(error);
                $('#msg-error').show();
            } else {

                for (let aId in answers) {
                    if (parseInt(aId) === 0) {
                        const keyElement = $(".key");
                        keyElement.show();
                        const reg = new RegExp("\\w*Klucze nie pokrywają się!\\w*");
                        if (reg.test(answers[aId].question)) {
                            keyElement.toggleClass("alert-danger alert-success");
                        }
                        keyElement.html("Verification Key: " + answers[aId].question);
                    } else {

                        answerName = "";
                        answerId = parseInt(aId);
                        let question = answers[aId];
                        let qName = question["question"];
                        if (!qIdArr.includes(question["id"])) {
                            qIdArr.push(question["id"]);
                            body += "<div id=" + question["id"] + " class='main-panel border rounded p-4'>";

                            body += "<div class='lead mb-2'>" + qName + "</div>";
                            body += "</div>";
                            $('body').append(body);
                        }

                        question["answers"].forEach(getAnswerName);
                        body = "";
                        if (question["type"] === 2) {
                            body += `<div>
                                <input type="radio" id='a` + answerId + `' checked disabled>
                                <label for='a` + answerId + `'>` + answerName + `</label>
                            </div>`;
                        } else if (question["type"] === 3) {
                            body += `<div>
                                <input type="checkbox" id='a` + answerId + `' checked disabled>
                                <label for='a` + answerId + `'>` + answerName + `</label>
                            </div>`;
                        } else {

                            body += "<div class='border pl-2 pt-2 pr-2 rounded' style='background-color: #eaeaea;'><p>" + answerName + "</p></div>";
                        }
                        $('#' + question["id"]).append(body);
                        body = "";

                    }
                }
            }
            let button = `<div class='main-panel border rounded p-4'>
                            <button class=\"btn btn-dark btn-block \" onclick=\"instantRedirectToProfile()\">Back</button>
                          </div>`;
            $("body").append(button);

        }
    };

    const token = $.cookie("token");
    let json = {"key": key, "uuid": token};
    let data = JSON.stringify(json);
    xhr.open('POST', 'http://localhost:8080/answerKey', false);
    xhr.setRequestHeader("Content-Type", "application/json; charset=UTF-8");
    xhr.send(data);
}

function getAnswerName(item) {
    if (item["id"] === answerId) {
        answerName = item["answer"];
    }
}