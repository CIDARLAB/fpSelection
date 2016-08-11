/*function testJetty() {
 $.ajax({
 url: "MainServlet",
 type: "POST",
 data: {
 "rank": document.getElementById("rank").value
 },
 success: function (response) {
 result = JSON.parse(response);
 alert("New rank is  (Rank + 1)::" + result.rank);
 },
 error: function () {
 alert("ERROR!!");
 }
 });
 }*/

$(document).ready(function () {
    $("#form").submit(function (event) {
        event.preventDefault();
        var url = $(this).attr('action');
        var formData = new FormData($('form')[0]);
        $.ajax({
            type: "POST",
            url: url,
            data: formData,
            cache: false,
            processData: false,
            contentType: false,
            success: function (response) {
                result = JSON.parse(response);
                document.getElementById("img").src = result.img;
                $("#p").append(result.info);
            }
        });
    });
    $("#exhaustiveForm, #somewhatExhaustiveForm, #hillClimbingForm, #simulatedAnnealingForm").submit(function (event) {
        event.preventDefault();
        var form = document.getElementById("goodForm");
        var url = $(this).attr('action');
        var formData = new FormData(form);
        $("#img").fadeOut(function () {
            $("#title").text("loading...");
            $("#SNR").text("");
        });
        $.ajax({
            url: url,
            type: "POST",
            data: formData,
            processData: false,
            contentType: false,
            success: function (response)
            {
                result = JSON.parse(response);
                $("#title").text("");
                document.getElementById("img").src = result.img;
                $("#img").fadeIn(function () {
                    $("#SNR").text(result.SNR);
                });
            }

        });
    });
});
