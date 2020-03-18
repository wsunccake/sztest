var drawCreatedAPI = function(opts) {
    let options = {
        chart: {
            renderTo: opts.containerId,
    //        type: 'spline'
            type: 'line'
        },
    
        title: {
            text: opts.title,
        },
    
        xAxis: {
            type: 'category',
        },
    
        yAxis: {
            title: {
                text: null
            }
        },
    
        tooltip: {
            crosshairs: true,
            shared: true,
            valueSuffix: 'ms'
        },
    
        legend: {
        },
    
        series: [],
    };

    let dataArray = [];
    let getData = function(csvFile, dataName) {
        return new Promise(function(resolve, reject) {
            let tmpArray = [];
            $.get(csvFile, function(csv) {
                let content_lines = csv.split("\n");
                for (let i in content_lines) {
                    let lines = content_lines[i].split(",");
                    tmpArray.push([lines[0], parseFloat(lines[1])]);
                }
                tmpArray.pop();
    
                dataArray.push(tmpArray);
                resolve(tmpArray);
                options.series.push({data: tmpArray, name: dataName});
            });
        });
    }
    
    
    async function getAllData() {
        for (let e of opts.seriesData) {
            await getData(opts.prefixDir + e + '.csv', e);
        }
        let chart = new Highcharts.Chart(options);
        console.log(dataArray);
    }
    
    getAllData();
}


var drawCreatedAPIWithRange = function(opts) {
    let options = {
        chart: {
            renderTo: opts.containerId,
    //        type: 'spline'
            type: 'line'
        },
    
        title: {
            text: opts.title,
        },
    
        xAxis: {
            type: 'category',
        },
    
        yAxis: {
            title: {
                text: null
            },
        },
    
        tooltip: {
            crosshairs: true,
            shared: true,
            valueSuffix: 'ms'
        },
    
        legend: {
        },
    
        series: [],
    };

    let dataArray = [];
    let iCount = 0;
    let getData = function(csvFile, dataName) {
        return new Promise(function(resolve, reject) {
            let averageArray = [];
            let rangeArray = [];
            $.get(csvFile, function(csv) {
                let content_lines = csv.split("\n");
                for (let i in content_lines) {
                    let lines = content_lines[i].split(",");
                    averageArray.push([lines[0], parseFloat(lines[1])]);
                    rangeArray.push([lines[0], parseFloat(lines[2]), parseFloat(lines[3])]);
                }
                averageArray.pop();
                rangeArray.pop();
    
                dataArray.push(averageArray);
                resolve(averageArray);
                options.series.push({data: averageArray,
                                     name: dataName,
                                     zIndex: 1,
                                     marker: {
                                         fillColor: 'white',
                                         lineWidth: 1,
                                         lineColor: Highcharts.getOptions().colors[iCount]
                                     }
                                    });
//                options.series.push({data: rangeArray,
//                                     name: dataName + ' range',
//                                     type: 'arearange',
//                                     linkedTo: ':previous',
//                                     color: Highcharts.getOptions().colors[iCount],
//                                     fillOpacity: 0.1,
//                                     zIndex: 0,
//                                     marker: { enabled: false },
//                                    });
                iCount++;
            });
        });
    }
    
    
    async function getAllData() {
        for (let e of opts.seriesData) {
            await getData(opts.prefixDir + e + '.csv', e);
        }
        let chart = new Highcharts.Chart(options);
        console.log(dataArray);
    }
    
    getAllData();
}

