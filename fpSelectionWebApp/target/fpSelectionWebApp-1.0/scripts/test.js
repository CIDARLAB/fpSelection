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

var laseField, filterField;

$(document).on('click', '.laser-btn', function () {
    $(".lasers").append("<br>" + laseField);
});

$(document).on('click', '.filter-btn', function () {
    $(this).parent().parent().append(filterField);
    $(this).remove();
});

$(document).ready(function () {
    $('.tooltipNav').tooltipster({theme: 'tooltipster-shadow'});
    $('.rightDiv, .semiExhaustive').tooltipster({
        theme: 'tooltipster-shadow',
        side: 'right',
        maxWidth: 400
    });
    $('.BSbtninfo').filestyle({
        buttonName: 'btn-info',
        buttonText: 'Browse',
        size: 'sm'
    });
    $("#algo").change(function () {
        $("#topPercent").toggle($(this).val() == "SomewhatExhaustiveServlet");
    });
    //Grab html for laser form
    laseField = $(".lasers").html();
    //Grab html for filter form
    filterField = $(".filters").html();

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
    $("#cytometerForm").submit(function (event) {
        event.preventDefault();

        var form = document.getElementById("cytometerForm");
        var url = "customCytometer";
        var formData = new FormData(form);

        $.ajax({
            url: url,
            type: "POST",
            data: formData,
            processData: false,
            contentType: false,
            success: function (response)
            {
                $(".responseDiv").text(response);
            }
        });
    });

    $("#MainForm").submit(function (event) {
        event.preventDefault();
        var url = $("#algo").val()
        var formData = new FormData(this);
        $("#title").text("Loading...");
        $("#SNR").text("");
        $("#placeholder").hide();
        $("#img").hide();
        var start = performance.now();

        $.ajax({
            url: url,
            type: "POST",
            data: formData,
            processData: false,
            contentType: false,
            success: function (response)
            {
                var end = performance.now();

                result = JSON.parse(response);
                document.getElementById("img").src = result.img;
                $("#SNR").text(result.SNR);
                $("#title").text("Time taken was: " + (end - start) / 1000 + " s");
                $("#img").show();
            }

        });
    });
});
