MATTER_FILE_NAME = "matter.txt"
DYNAMIC_FILE_NAME = "dynamic.txt"
STATIC_FILE_NAME = "static.txt"

PARTICLE_XYZ_MOVEMENT_FILE_NAME = "particle_movement.xyz"
MATTER_XYZ_FILE_NAME = "matter.xyz"



def generate_particle_movement():

    particle_movement_output_file = open(PARTICLE_XYZ_MOVEMENT_FILE_NAME,'w')

    with open(DYNAMIC_FILE_NAME) as f:
        for index, line in enumerate(f):
            # x y vx vy
            line = line.split()
            particle_movement_output_file.write(f"1\nCOMMENT\n{' '.join(line)} -1\n")
    
    particle_movement_output_file.close()


def generate_matter():
  
  static_data_file = open(STATIC_FILE_NAME,'r')

  number_of_particles = int(static_data_file.readlines()[1]) ** 2

  matter_output_file = open(MATTER_XYZ_FILE_NAME,'w')
  matter_output_file.write(f"{number_of_particles}\nCOMMENT\n")

  with open(MATTER_FILE_NAME,'r') as f:

    for line in f:
      matter_output_file.write(line)

  matter_output_file.close()

def main():
  generate_matter()
  generate_particle_movement()
  

if __name__ == "__main__":
  main()