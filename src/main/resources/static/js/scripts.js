let polygonStockApiUrl = ["https://api.polygon.io/v2/aggs/ticker/", "/range/1/day/", "/", "?apiKey=5Jgi3YlL3jI6A3LAENnC1qqs7ebQz4HU"]
let alphaVantageApiUrl = ["https://www.alphavantage.co/query?function=OVERVIEW&symbol=","&apikey=12JWP14CSW66DSLG"]
document.addEventListener("DOMContentLoaded", function() {

    const searchButton = document.querySelector(".search-button");
    const chartImage = document.querySelector(".chart-image");
    const companyImage = document.querySelector(".company-image");

    searchButton.addEventListener("click", prepareData);

    async function prepareData() {
        var companyName = document.querySelector(".company-name").value;
        var stockName = document.querySelector(".stock-value").value;
        var startDate = document.querySelector(".start-date").value;
        var endDate = document.querySelector(".end-date").value;

        console.log(stockName);
        console.log(startDate);
        console.log(endDate);

        try {
            let apiResponse = await fetchData(stockName, startDate, endDate);
            console.log("API CALL completed");
            console.log(apiResponse); // this is JSON format.

//          Send stockData over to backend java
            let processResponse = await sendToServer(apiResponse, startDate, endDate);

            console.log("Server processing completed", processResponse);
            console.log("Now calling getImage");

//          Get image url from backend java
            let chartImageUrl = await getImageUrl();

//          Get company logo from ticker: AAPL --> apple.com --> logo
//            let companyDomain = await getDomainFromTicker(stockName);
            companyImage.src = "https://logo.clearbit.com/" + companyName + ".com";

            if (chartImageUrl) {
                console.log("trying to change source")
                chartImage.src = chartImageUrl;
            }

            let tables = await getTableData();
            console.log("function is done 0")
            console.log("Function is done");
        } catch (error) {
            console.error("Error fetching or sending data");
        }
        }

    async function fetchData(stock_name, start_date, end_date) {
    //"https://api.polygon.io/v2/aggs/ticker/AAPL/range/1/day/2022-10-10/2023-01-09?apiKey=5Jgi3YlL3jI6A3LAENnC1qqs7ebQz4HU"
        let updatedUrl = polygonStockApiUrl[0] + stock_name + polygonStockApiUrl[1] + start_date + polygonStockApiUrl[2] + end_date + polygonStockApiUrl[3];
        console.log(updatedUrl);

        const response = await fetch(updatedUrl);
        if (!response.ok) {
            throw new Error('Network response was not ok: ' + response.statusText);
        }
        return await response.json();
    }

    async function sendToServer(data, startDate, endDate) {
        const requestBody = {
              ...data,
              startDate: startDate,
              endDate: endDate
        };
        const response = await fetch('/processStockData', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(requestBody)
        });

        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        return await response.text();
    };

    async function getImageUrl() {

        try {
            const response = await fetch('/chart');
            const blob = await response.blob();
            const imageUrl = URL.createObjectURL(blob);
            console.log("image below")
            console.log(imageUrl);
            return imageUrl;
        } catch (error) {
            console.error("Error fetching the chart", error)
        }
    };

// Function to fetch data from backend and update table
    async function getTableData() {
        console.log("i am called");
        try {
            const response = await fetch('/table');
            if (!response.ok) {
                throw new Error('Network response was not ok.');
            }
            const data = await response.json();

            console.log("Stored data");
            console.log(data);
            // Select the table body where data will be inserted
            const tableBody = document.getElementById("tableBody");

            // Clear existing rows (if any)
            tableBody.innerHTML = '';

             for (const [companyTicker, companyObj] of Object.entries(data)) {
                const row = document.createElement('tr');
                row.innerHTML = `
                      <td>${companyObj.stockName}</td>
                      <td>${companyObj.startDate}</td>
                      <td>${companyObj.endDate}</td>
                      <td>${companyObj.changeResult}</td>
                `;
                tableBody.appendChild(row);
             }
            // Create a single row for the stock data

            return tableBody;
        } catch (error) {
            console.error('Error fetching data:', error);
        }
    }


    async function getDomainFromTicker(stock_name) {
        let stockApiUrl = alphaVantageApiUrl[0] + stock_name + alphaVantageApiUrl[1];
        const response = await fetch(stockApiUrl);

        console.log(stockApiUrl);
        if (response.ok) {
            console.log("domain api call successful")
            const data = await response.json();
            console.log(data);
            let companyName = data.Name
            companyName = companyName.split(" ")
            companyName = companyName[0]
            companyName = companyName.toLowerCase()
            console.log(companyName);
            return companyName;
        } else {
            return null;
        }

    };
});
