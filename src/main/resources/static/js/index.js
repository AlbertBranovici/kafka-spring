
const endpoint_url = 'https://api-m.sandbox.paypal.com';

function get_access_token(){
    const auth = `${client_id}:${client_secret}`;
    const data = 'grant_type=client_credentials';
    return fetch(endpoint_url + '/v1/oauth2/token',{
        method:'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            'Authorization': `Basic ${Buffer.from(auth).toString('base64')}`
        },
        body: data
    })
        .then(res => res.json())
        .then(json => {return json.access_token;})
}



document.addEventListener("DOMContentLoaded", function() {
    const flightForm = document.getElementById('flightForm');
    const resultsDiv = document.getElementById('results');
    const flightList = document.getElementById('flightList');
    const flightDetailsDiv = document.getElementById('flightDetails');
    const selectedFlight = document.getElementById('selectedFlight');
    const bookingForm = document.getElementById('bookingForm');
    const flightIdInput = document.getElementById('flightId');
    const flightPriceInput = document.getElementById('flightPrice');
    const seatsInput = document.getElementById('seatsInput');

    flightForm.addEventListener('submit', function(event) {
        console.log("se face CEVA!");
        event.preventDefault();

        const destinationInput = document.getElementById('destination').value.toUpperCase();
        flightList.innerHTML = '';
        const url = `http://localhost:8081/api/v1/flight/destination/${destinationInput}`;


        fetch(url)
            .then(response=>response.json())
            .then(flights => {

               if (flights.length > 0){
                   flights.forEach(flight => {
                       const li = document.createElement('li');
                       li.className = 'list-goup-item';
                       li.textContent = `Flight ${flight.flightNumber} - ${flight.company} - Click for details`;
                       li.dataset.id = flight.idflights;
                       li.dataset.price = flight.price;
                       li.addEventListener('click', function(){
                           selectedFlight.innerHTML = `
                                <strong>Flight Number:</strong> ${flight.flightNumber}<br>
                                <strong>Company:</strong> ${flight.company}<br>
                                <strong>Departure:</strong> ${flight.departureLocation} (${flight.departureTime})<br>
                                <strong>Arrival:</strong> ${flight.arrivalLocation}<br>
                                <strong>Terminal:</strong> ${flight.terminal}<br>
                                <strong>Priceee:</strong> $${flight.price}<br>
                                <strong>Available seats:</strong> ${flight.totalSeats}
                        
                            `;
                           flightDetailsDiv.style.display = 'block';
                           flightIdInput.value = flight.idflights;
                           selectedFlightPrice = flight.price;
                           updateTotalPrice();
                       });
                       flightList.appendChild(li);
                   });
                   resultsDiv.style.display = 'block';
               } else {
                   flightList.innerHTML = '<li class="list-group-item">No flights available</li>';
               }
            })
            .catch(error => {
                console.error("Error fetching data: ", error);
                flightList.innerHTML = '<li class="list-group-item">No flights found</li>';
            });
    });

    function updateTotalPrice() {
        const seats = parseInt(seatsInput.value, 10);  // Get the number of seats as an integer
        if (!isNaN(seats) && seats > 0) {
            const totalPrice = selectedFlightPrice * seats;
            flightPriceInput.value = totalPrice;  // Update the total price input
        } else {
            flightPriceInput.value = 0;  // If the seat input is invalid, reset the price
        }
    }
    seatsInput.addEventListener('input', updateTotalPrice);

    bookingForm.addEventListener('submit', function(event) {
        event.preventDefault();

        // Get values from the form
        const name = document.getElementById('name').value;
        const flightId = document.getElementById('flightId').value; // Make sure the correct ID is used for flightId input
        const flightPrice = document.getElementById('flightPrice').value;
        const iban = "ROSKY1";
        const seats = document.getElementById('seatsInput').value;

        // Debugging alert to show the flight info
        // alert(`Booking confirmed for ${name} with Flight ID: ${flightId}`);

        // Send POST request with fetch
        fetch('http://localhost:8081/api/v1/booking/add', {
            method: "POST",
            body: JSON.stringify({
                "full_name": name,
                "flight": {
                    "idflights": flightId
                },
                "seats": seats
            }),
            headers: {
                "Content-Type": "application/json; charset=UTF-8"
            }
        })
            .then(response => response.json())
            .then(data => {
                if(data.status === "success" && data.redirectUrl){
                    window.location.href = data.redirectUrl;
                } else {
                    console.error("Error: approval url not found")
                }
            })
            .catch((error) => {
                console.error('Error:', error);  // Handle errors
            });
    });

});
