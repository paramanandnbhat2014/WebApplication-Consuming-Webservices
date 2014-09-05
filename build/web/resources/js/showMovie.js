//side bar slide control
$(document).ready(function() {
    $("#flip1").click(function() {
        $("#panel1").slideToggle("slow");
    });
    $("#flip2").click(function() {
        $("#panel2").slideToggle("slow");
    });
});

// about&share control
$(document).ready(function() {
    $("#about").slideDown("fast");
    $("#share").slideUp("fast");
    $("#timeline").slideUp("fast");
    $("#nav-about").click(function() {
        $("#nav-share").removeClass('current');
        $("#nav-about").addClass('current');
        $("#about").slideDown("slow");
        $("#share").slideUp("slow");
    });
    $("#nav-share").click(function() {
        $("#nav-about").removeClass('current');
        $("#nav-share").addClass('current');
        $("#share").slideDown("slow");
        $("#about").slideUp("slow");
    });
    $("#sub").click(function(){
        $("#timeline").slideDown("slow");
    });
});



