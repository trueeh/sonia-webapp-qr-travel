function showCommitMessage()
{

  formPin = $("#PIN").val();
  formMail = $("#mail").val();
  formGivenName = $("#givenname").val();
  formSureName = $("#surename").val();

  $("#valuesForMessage").html(
          "<p>" + formPin + "</p>" +
          "<p>" + formMail + "</p>" +
          "<p>" + formGivenName + "</p>" +
          "<p>" + formSureName + "</p>"
          );

  $('#sendDataModalMessage').modal('show');
  setTimeout(function () {
    $('#sendDataModalMessage').modal('hide');
  }, 4000);

}

function checkAndSend(formObject)
{
  //check pin
  if (formObject.PIN.value.length != 6)
  {
    $("#invalidEntryModalMessageInfoText").html("Die eingetragene Raum Pin ist zu kurz.");
    $('#invalidEntryModalMessage').modal('show');
    return;
  }
  // check mail
  if (!/^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/.test(formObject.mail.value))
  {
    $("#invalidEntryModalMessageInfoText").html("Die eingetragene Mail ist ungültig.");
    $('#invalidEntryModalMessage').modal('show');
    return;
  }

  //check phone number

  wrong = false;
  if (!/^\+?([0-9]*)\)?[-. ]?([0-9]*)[-. ]?([0-9]*)$/.test(formObject.phonenumber.value))
  {
    wrong = true;
  }

  if (formObject.phonenumber.value.length < 5)
  {
    wrong = true;
  }

  if (wrong)
  {
    $("#invalidEntryModalMessageInfoText").html("Die eingetragene Telefonnummer ist ungültig.");
    $('#invalidEntryModalMessage').modal('show');
    return;
  }

  showCommitMessage();
  formObject.submit();

}

function checkAndSendForNoPin(formObject)
{
  if (formObject.pin.value.length < 6)
  {
    return;
  }

  formObject.submit();
}