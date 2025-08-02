Task 2: Answer follow-up questions

1. if I had more time, I would add the following:

    * Finish the feature for handling empty date ranges. In my case, when I was testing I found that if I select a 6 months range, the app only displays data for four months with transactions (May, June, July and Aug). I would like to develop the logic that fills these gaps by showing empty months as Zeros, making the chart more accurate.
    * Finish the currency feature, I added the frontend dropdown menu for currency selection, but the backend functionality is still pending. The next steps include creating the backend logic to fetch the rate from local DB, develop a service layer using an interface, expose the API, update the react component and test it.
    * Add unit and integration tests using JUnit and mockito to improve the code coverage and the reliability.
    * Optimize database queries with proper indexing and pagination, handle the date grouping and spending calculations directly in the database when the volumen of the data is big enough and requires it, to reduce memory usage, improve performance and minimize bandwith consume.
    * Implement consistent error handling and clear input/output validation across all Api endpoints, and add proper authentication and authorization to ensure security and reliability.
2: After some research I will sugest implement a Backend service that connects to XE and retrieves the latest exchange rates, more info:

    * The Backend have to save into a local database the new exchange rate, this values will be updated every 6-12H according to the needs. This avoid overloading the Api and ensures that the data is reasonably fresh.
    
    * On-demand with caching, when a request comes in, check if the stored exchange rate is older than a set threshold (like 1H). If it's too old, fetch the latest rate from XE. update the cache and database. This option is an addition from the previous one and provide more fresh and accurate exchange rates.

3: To reduce unnecessary calls to /api/currencies, I would use a shared data strategy. First, I would fetch the currency list one when the app loads and store it either in a shared state (Like Redux o React Context) or in local storage. For extra safety, we can add a simple refresh check, if data > 24H then fetch it again. This keeps the data up to date without overloading the server.


Thank you for reviewing my proposal. I hope these ideas help to  improve flexibility and preformance.