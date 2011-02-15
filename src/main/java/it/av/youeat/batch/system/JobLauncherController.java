package it.av.youeat.batch.system;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Launchs the batch Ristorant import 
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 *
 */
public class JobLauncherController {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job job;

    /**
     * Run the import
     * 
     * @throws Exception
     */
    public void run() throws Exception {
        //TODO correct parameters...
        jobLauncher.run(job, new JobParameters());
    }

}
