select  a1.Airport_ID, a1.Name, a2.Airport_ID, a2.Name
from airport a1, airport a2, airline, route
where airline.Airline_ID=596
and route.Airline_ID=airline.Airline_ID
and route.Source_airport_ID=a1.Airport_ID
and route.Destination_airport_ID=a2.Airport_ID