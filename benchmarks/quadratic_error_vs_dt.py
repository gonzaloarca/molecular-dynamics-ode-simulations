import os
    import time
import numpy as np
import matplotlib.pyplot as plt


def get_results():
    results = []
    with open('./output.csv', 'r') as f:
        for line_number, line in enumerate(f):
            if line_number == 0:
                methods = line.split(";")
                for method in methods:
                    results.append([])
            else:
                output = line.split(";")
                for index, method in enumerate(methods):
                    results[index].append(float(output[index]))
    return results


def get_quadratic_error(method_results, analytic_result):
    np_analytical_result = np.array(analytic_result)
    quadratic_errors = []
    for result in method_results:
        method_results = np.array(result)
        quadratic_error = (
            np.sum(np.square(method_results - np_analytical_result)) / len(result))
        quadratic_errors.append(quadratic_error)

    return quadratic_errors


def quadratic_error_vs_dt():
    dts = np.logspace(-5, -1, 1000)
    quadratic_errors = []
    startTime = time.perf_counter()
    for dt in dts:
        cmd = f"java -DstepSize={dt} -classpath ./target/classes ar.edu.itba.ss.moleculardynamics.dampedharmonicoscillator.Simulation"
        print(cmd)
        os.system(cmd)
        results = get_results()
        quadratic_errors.append(get_quadratic_error(results[1:], results[0]))

    for quadratic_error in (np.array(quadratic_errors)).T:
        plt.plot(dts, quadratic_error)

    end_time = time.perf_counter()
    print(f"Time: {end_time - startTime}")
    plt.legend(["Verlet", "Beeman", "Gear-Predictor"])
    plt.yscale('log')
    plt.xscale('log')
    plt.show()


if __name__ == '__main__':
    quadratic_error_vs_dt()
