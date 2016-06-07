select distinct AirportId from (
select distinct r1.Source_airport_ID as AirportId
from route r1
where r1.Airline="LH"
union
select distinct r2.Destination_airport_ID as AirportId
from route r2
where r2.Airline="LH"
) as ports
