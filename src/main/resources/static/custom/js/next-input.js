function nextInput (inputField,target)
{

    //alert("hello from nextInput");
    if ( inputField.name == "PIN" )
    {
        if (inputField.value.length == 6 )
        {
            target.focus();
            target.select();
        }

        return;
    }
    
    if ( inputField.name == "mail" )
    {
      if (! /^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/.test(inputField.value))
      {
        $( "#invalidEntryModalMessageInfoText" ).html( "Die eingetragene Mail ist ungültig." );
        $('#invalidEntryModalMessage').modal('show');
        return;
      }
    }

    if ( inputField.name == "phonenumber" )
    {
        wrong=false;
      if (! /^\+?([0-9]*)\)?[-. ]?([0-9]*)[-. ]?([0-9]*)$/.test(inputField.value))
      {
        wrong = true;
      }

      if ( inputField.value.length < 5 )
      {
          wrong=true;
      }

      if ( wrong )
      {
        $( "#invalidEntryModalMessageInfoText" ).html( "Die eingetragene Telefonnummer ist ungültig." );
        $('#invalidEntryModalMessage').modal('show');
        return;
      }

    }



    if ( inputField.name == "mail" ||  inputField.name == "givenname" ||  inputField.name == "phonenumber" )
    {   
            target.focus();
            target.select();   
        return;
    }

}