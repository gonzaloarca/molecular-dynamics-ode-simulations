import os
import time
import numpy as np
import matplotlib.pyplot as plt


def sci_notation(number, sig_fig=2):
    ret_string = "{0:.2e}".format(number, sig_fig)
    a, b = ret_string.split("e")
    # remove leading "+" and strip leading zeros
    b = int(b)
    return a + f"$\\times 10^{{{b}}}$"


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
    dts = np.logspace(-5, -1, 100)
    quadratic_errors = []
    startTime = time.perf_counter()

    output_file = open('./output_files/ECM.csv', 'w')

    for dt in dts:
        cmd = f"java -DstepSize={dt} -jar ./target/molecular-dynamics-ode-simulations-1.0-SNAPSHOT.jar"
        print(cmd)
        os.system(cmd)
        results = get_results()
        quadratic_errors.append(get_quadratic_error(results[1:], results[0]))

    for dt, quadratic_error in zip(dts, quadratic_errors):
        output_file.write(f"{dt} {' '.join(map(str,quadratic_error))}\n")

    end_time = time.perf_counter()
    print(f"Time: {end_time - startTime}")
    output_file.close()

    return dts, quadratic_errors


def get_quadratic_error_from_file():

    input_file = open('./output_files/ECM.csv', 'r')
    dts, quadratic_errors = [], []
    for line in input_file:
        line_data = line.split()
        dts.append(float(line_data[0]))
        quadratic_errors.append(list(map(float, line_data[1:])))

    return dts, quadratic_errors


def plot_quadratic_error_vs_dt(dts, quadratic_errors):
    fig, ax = plt.subplots()

    for quadratic_error in (np.array(quadratic_errors)).T:
        ax.plot(dts, quadratic_error)

    plt.yscale('log')
    plt.xscale('log')

    ax.xaxis.set_major_formatter(
        plt.FuncFormatter(sci_notation))

    ax.yaxis.set_major_formatter(
        plt.FuncFormatter(sci_notation))

    plt.xlabel('dt [$s$]', fontsize=25)
    plt.ylabel('Error cuadrático medio [$m^{2}$]', fontsize=25)

    plt.xticks(fontsize=20)
    plt.yticks(fontsize=20)

    plt.legend(["Verlet", "Beeman", "Gear Predictor-Corrector"], fontsize=25)
    plt.show()


if __name__ == '__main__':
    # dts, quadratic_errors = quadratic_error_vs_dt()
    dts, quadratic_errors = get_quadratic_error_from_file()
    plot_quadratic_error_vs_dt(dts, quadratic_errors)
