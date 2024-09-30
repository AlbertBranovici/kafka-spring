const urlParams = new URLSearchParams(window.location.search);

const token = urlParams.get('token');
const iban = "ROSKY1";

if (token){
    fetch(`http://localhost:8081/paypal/capture?token=${encodeURIComponent(token)}&iban=${encodeURIComponent(iban)}`, {
       method: "POST",
       headers: {
           "Content-Type": "application/json; charset=UTF-8"
       }
    })
        .then(response => response.json())
        .then(data => {
            if(data && data.status==="COMPLETED"){
                console.log("Payment completed", data);

                alert("Payment completed");
            } else {
                console.log("Roor:  payment not ok", data);
            }
        })
        .catch((error) =>{
            console.log("Error: ", error);
        });
} else {
    console.error("Token is missing", token);
}