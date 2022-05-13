from pprint import pprint
import numpy as np
from matplotlib import pyplot as plt


def sci_notation(number, sig_fig=2):
    ret_string = "{0:.2e}".format(number, sig_fig)
    a, b = ret_string.split("e")
    # remove leading "+" and strip leading zeros
    b = int(b)
    return a + f"$\\times 10^{{{b}}}$"


def plot_methods(total_time, step_size: float, results: list[list], methods: list[str]):
    x = np.arange(0, total_time, step_size)
    fig, ax = plt.subplots()
    colors = ['darkblue', 'orange', 'red']
    results = np.array(results)
    quadratic_errors = []
    for i, result in enumerate(results[1:]):
        quadratic_error = np.sum(np.square(result - results[0])) / len(result)
        quadratic_errors.append(quadratic_error)
        plt.plot(x, result, color=colors[i])
    plt.plot(x, results[0], '--', color='cyan')

    # ax.xaxis.set_major_formatter(
    #     plt.FuncFormatter(sci_notation))
    # ax.yaxis.set_major_formatter(
    #     plt.FuncFormatter(sci_notation))
    new_methods = methods[1:]
    new_methods.append(methods[0])
    plt.legend(new_methods, fontsize=25)
    plt.xlabel('Tiempo [s]', fontdict={'fontsize': '25'})
    plt.ylabel('Posición [m]', fontdict={'fontsize': '25'})
    plt.xticks(fontsize=25)
    plt.yticks(fontsize=25)
    plt.ylim(top=1.5, bottom=-1.5)
    plt.tight_layout(pad=1)
    pprint(quadratic_errors)
    plt.show()


if __name__ == '__main__':
    results = []
    with open('output.csv', 'r') as f:
        for line_number, line in enumerate(f):
            if line_number == 0:
                methods = line.strip().split(";")
                for method in methods:
                    results.append([])
            else:
                output = line.split(";")
                for index, method in enumerate(methods):
                    results[index].append(float(output[index]))

    plot_methods(5, 0.01, results, methods)
