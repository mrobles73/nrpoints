const homeBtn = document.querySelector("#home-a");
const aboutBtn = document.querySelector("#about-a");

var inputs = document.querySelectorAll( '.race-files' );
Array.prototype.forEach.call( inputs, function( input )
{
	var label = input.nextElementSibling, labelVal = label.innerHTML;

	input.addEventListener( 'change', function( e )
	{
		var fileName = '';
		if( this.files && this.files.length > 1 )
			fileName = ( this.getAttribute( 'data-multiple-caption' ) || '' ).replace( '{count}', this.files.length );
		else
			fileName = e.target.value.split( '\\' ).pop();

		if( fileName )
			label.innerHTML = fileName;
		else
			label.innerHTML = labelVal;
	});
});

const selectNavLink = (title) => {
    if(title === "nrpoints") {
        homeBtn.classList.add("nav-active");
        aboutBtn.classList.remove("nav-active");
    } else if(title === "About"){
        aboutBtn.classList.add("nav-active");
        homeBtn.classList.remove("nav-active");
    } else {
        aboutBtn.classList.remove("nav-active");
        homeBtn.classList.remove("nav-active");
    }
};

function readFile(file) {
    return new Promise(function(resolve, reject) {
        let reader = new FileReader();
        reader.onload = () => {
            if(reader.result === '') {
                reject(new Error("A file passed in was empty. Please try again."));
            } else {
                resolve(reader.result);
            }
        }
        reader.onerror = () => {
            reject(new Error("Error reading file. Please try again."));
        }
        reader.readAsText(file);
    });
}

function validateFilesSelected(files) {
    if(files.length === 0)
        return false;
    else {
        for(let file of files) {
            if(file.type != 'text/html') {
                showErrorMessage('Wrong file type. Please try again.');
                return false;
            }
        }
        return true;
    }
}

function showErrorMessage(message) {
    document.querySelector("#error-alert").classList.remove('hidden');
    document.querySelector("#error-message").innerHTML = message;
}

function hideErrorMessage() {
    document.querySelector("#error-alert").classList.add('hidden');
}

window.addEventListener("load", () => {
    let pageTitle = document.title;
    selectNavLink(pageTitle);

    document.querySelectorAll(".close-alert").forEach((closeAlert) => {
        closeAlert.addEventListener("click", () => {
            closeAlert.parentElement.classList.add('hidden');
        });
    });

});


//ways to post data from index html
//$.ajax({
//                type: "POST",
//                url: "/sendfiles",
//                data: "",
//                success: function() {
//                    alert("post request successfull");
//                }
//            });
//
//
//                        var dataString = $(this).serialize();
//            console.log(dataString);
//            const files = $("#race-files").prop('files');
//            console.log(files);
//            var formData = new FormData();
//            formData.append("yearSeriesData", dataString);
//            for(var file of files) {
//                formData.append(file.name, file);
//            }
//            const uri = "/sendfiles";
//            const xhr = new XMLHttpRequest();
//            xhr.open("POST", uri, true);
//            xhr.onreadystatechange = function () {
//            if (xhr.readyState === 4 && xhr.status === 200) {
//               console.log(xhr.status);
//               console.log(xhr.responseText);
//            }};
//
//            xhr.send(dataString);
//
//            $.ajax({
//                type: "POST",
//                url: "/sendfiles",
//                data: dataString,
//                success: function(responseData) {
//                    alert("post request successfull");
//                }
//            });
